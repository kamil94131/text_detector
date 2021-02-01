package com.image.design.textdetector.service;

import com.image.design.textdetector.configuration.MessageResource;
import com.image.design.textdetector.model.protocol.CodeResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CodeService {

    private static final Logger LOGGER = Logger.getLogger(CodeService.class.getName());
    private static final String LETTERS_AND_DIGITS_REGEX = "[^a-zA-Z0-9]";
    private static final String DIGITS_REGEX = "[0-9]+";

    private static final int DIGITS_COUNT = 5;

    @Value("${detectedcode.prefix}")
    private String detectedCodePrefix;

    private final MessageResource messageResource;

    public CodeService(MessageResource messageResource) {
        this.messageResource = messageResource;
    }

    private static final Map<Character, Character> mappingMap = Map.of(
            'S', '5', 'A', '4', 'T', '1', 'B', '8', 'C', '0', 'O', '0'
    );

    public CodeResult convert(final String rawCode) {

        if(rawCode.isBlank()) {
            return new CodeResult("", rawCode);
        }

        final String codeWithLettersAndNumbers = rawCode.replaceAll(LETTERS_AND_DIGITS_REGEX, "");

        if(codeWithLettersAndNumbers.isBlank() || codeWithLettersAndNumbers.length() < DIGITS_COUNT) {
            return new CodeResult(this.messageResource.get("imagedesign.detect.wrong.code", rawCode), rawCode);
        }

        final String digits = codeWithLettersAndNumbers.substring(codeWithLettersAndNumbers.length() - DIGITS_COUNT);
        final String processedDigits = mapCode(digits);

        final Pattern digitsPattern = Pattern.compile(DIGITS_REGEX);
        final String code = String.format("%s%s", this.detectedCodePrefix, processedDigits);

        if(!digitsPattern.matcher(processedDigits).matches()) {
            return new CodeResult(this.messageResource.get("imagedesign.detect.wrong.code", rawCode), rawCode);
        }
        return new CodeResult("", code);
    }

    private String mapCode(final String digits) {
        if(digits.isBlank()) {
            return digits;
        }

        return digits.chars().mapToObj(digitCode -> {
            final char digit = (char) digitCode;

            if(Character.isLetter(digit) && mappingMap.containsKey(digit)) {
                return mappingMap.get(digit);
            }

            return digit;
        }).map(String::valueOf).collect(Collectors.joining());
    }
}
