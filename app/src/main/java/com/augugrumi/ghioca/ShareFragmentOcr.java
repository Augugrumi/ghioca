package com.augugrumi.ghioca;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class ShareFragmentOcr extends ShareFragment {
    @Override
    protected String shareContent() {
        ArrayList<String> results = ((OCRResultActivity)getActivity()).getText();
        String description = ((OCRResultActivity)getActivity()).getLanguage();

        StringBuilder toShare = new StringBuilder(description);
        toShare.append("\n");
        for (String res : results) {
            toShare.append(WordUtils.uncapitalize((WordUtils.capitalize(res))));
        }

        toShare.append("#GhioCa");

        return toShare.toString();
    }
}
