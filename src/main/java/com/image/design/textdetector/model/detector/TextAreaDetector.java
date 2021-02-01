package com.image.design.textdetector.model.detector;

import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.model.file.FileExtension;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Component
public class TextAreaDetector {

    private static final Logger LOGGER = Logger.getLogger(TextAreaDetector.class.getName());

    @Value("${thresh.score}")
    private float score;

    @Value("${thresh.nms}")
    private float nms;

    private final Net frozenEastNeuralNetwork;
    private final MessageResource messageResource;

    public TextAreaDetector(final Net frozenEastNeuralNetwork, final MessageResource messageResource) {
        this.frozenEastNeuralNetwork = frozenEastNeuralNetwork;
        this.messageResource = messageResource;
    }

    public byte[] detect(byte[] data, final FileExtension fileExtension) {
        try {
            final Net net = this.frozenEastNeuralNetwork;
            final Mat frame = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.IMREAD_UNCHANGED);

            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);

            final Size size = new Size(320, 320);
            final List<Mat> outs = new ArrayList<>(2);

            int H = (int)(size.height / 4);
            Mat blob = Dnn.blobFromImage(frame, 1.0,size, new Scalar(255, 0, 255), true, false);
            net.setInput(blob);
            net.forward(outs, getOutputNNLayerNames());

            Mat scores = outs.get(0).reshape(1, H);
            Mat geometry = outs.get(1).reshape(1, 5 * H);
            List<Float> confidencesList = new ArrayList<>();
            List<RotatedRect> boxesList = decode(scores, geometry, confidencesList, score);

            if(confidencesList.isEmpty()) {
                return data;
            }

            MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confidencesList));
            RotatedRect[] boxesArray = boxesList.toArray(new RotatedRect[0]);
            MatOfRotatedRect boxes = new MatOfRotatedRect(boxesArray);
            MatOfInt indices = new MatOfInt();
            Dnn.NMSBoxesRotated(boxes, confidences, score, nms, indices);

            final Point[] detectedRightBottomPoints = getDetectedRightBottomPoints(frame, size, boxesArray, indices);

            if(Objects.isNull(detectedRightBottomPoints)) {
                return new byte[0];
            }

            int x = (int)detectedRightBottomPoints[1].x;
            int y = (int)detectedRightBottomPoints[1].y;
            int width = (int)(detectedRightBottomPoints[2].x - detectedRightBottomPoints[1].x);
            int height = (int)(detectedRightBottomPoints[0].y - detectedRightBottomPoints[1].y);

            if(x + width > frame.cols()) {
                width = frame.cols() - x;
            }

            if(y + height > frame.rows()) {
                height = frame.rows() - y;
            }

            final Mat mat = frame.submat(new Rect(x, y, width, height));

            final MatOfByte matByte = new MatOfByte();
            final String extension = String.format(".%s", fileExtension.name().toLowerCase());
            Imgcodecs.imencode(extension, mat, matByte);

            return matByte.toArray();
        } catch(Exception e) {
            LOGGER.warning(this.messageResource.get("system.error.opencv.unknown.problem", e.toString()));
            return new byte[0];
        }
    }

    private static List<RotatedRect> decode(Mat scores, Mat geometry, List<Float> confidences, float scoreThresh) {
        int W = geometry.cols();
        int H = geometry.rows() / 5;

        List<RotatedRect> detections = new ArrayList<>();
        for (int y = 0; y < H; ++y) {
            Mat scoresData = scores.row(y);
            Mat x0Data = geometry.submat(0, H, 0, W).row(y);
            Mat x1Data = geometry.submat(H, 2 * H, 0, W).row(y);
            Mat x2Data = geometry.submat(2 * H, 3 * H, 0, W).row(y);
            Mat x3Data = geometry.submat(3 * H, 4 * H, 0, W).row(y);
            Mat anglesData = geometry.submat(4 * H, 5 * H, 0, W).row(y);

            for (int x = 0; x < W; ++x) {
                double score = scoresData.get(0, x)[0];
                if (score >= scoreThresh) {
                    double offsetX = x * 4.0;
                    double offsetY = y * 4.0;
                    double angle = anglesData.get(0, x)[0];
                    double cosA = Math.cos(angle);
                    double sinA = Math.sin(angle);
                    double x0 = x0Data.get(0, x)[0];
                    double x1 = x1Data.get(0, x)[0];
                    double x2 = x2Data.get(0, x)[0];
                    double x3 = x3Data.get(0, x)[0];
                    double h = x0 + x2;
                    double w = x1 + x3;
                    Point offset = new Point(offsetX + cosA * x1 + sinA * x2, offsetY - sinA * x1 + cosA * x2);
                    Point p1 = new Point(-1 * sinA * h + offset.x, -1 * cosA * h + offset.y);
                    Point p3 = new Point(-1 * cosA * w + offset.x,      sinA * w + offset.y); // original trouble here !
                    RotatedRect r = new RotatedRect(new Point(0.5 * (p1.x + p3.x), 0.5 * (p1.y + p3.y)), new Size(w + 10, h + 10), -0.5 * angle * 180 / Math.PI);
                    detections.add(r);
                    confidences.add((float) score);
                }
            }
        }
        return detections;
    }

    private Point[] getDetectedRightBottomPoints(final Mat frame, final Size size, final RotatedRect[] boxesArray, final MatOfInt indices) {
        final Point ratio = new Point((float)frame.cols() / size.width, (float)frame.rows() / size.height);
        int[] indexes = indices.toArray();

        Point[] rightBottomDetectionPoints = null;

        for (int index : indexes) {
            final RotatedRect rot = boxesArray[index];
            final Point[] vertices = new Point[4];

            rot.points(vertices);

            for (int j = 0; j < 4; ++j) {
                vertices[j].x *= ratio.x;
                vertices[j].y *= ratio.y;
            }

            if (Objects.isNull(rightBottomDetectionPoints) || calculateVectorValue(vertices) > calculateVectorValue(rightBottomDetectionPoints)) {
                rightBottomDetectionPoints = vertices;
            }
        }
        return rightBottomDetectionPoints;
    }

    private double calculateVectorValue(final Point[] vertices) {
        // calculate vector length between left top (0,0) and max detected border on right bottom
        // => max vector value mean that we detected paper with code on right bottom
        // we are looking for max bcs algorithm can detect more number on image,
        // and we know that paper will be on right bottom
        return Math.sqrt(Math.pow(vertices[0].x, 2) + Math.pow(vertices[0].y, 2));
    }

    private List<String> getOutputNNLayerNames() {
        return List.of("feature_fusion/Conv_7/Sigmoid", "feature_fusion/concat_3");
    }


}
