package com.augugrumi.ghioca.asyncTask.asynkTaskResult;

import it.polpetta.libris.opticalCharacterRecognition.contract.IOcrSearchResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */

public class FreeOcrSpaceOCRResult implements IOcrSearchResult {

    private ArrayList<String> parsedText;

    public FreeOcrSpaceOCRResult(String jsonFormatString) {
        parsedText = new ArrayList<>();
        decodeJSON(jsonFormatString);
    }

    private void decodeJSON(String toDecode) {
        try {
            JSONObject toDecodeJSON = new JSONObject(toDecode);
            JSONArray parsedResults = toDecodeJSON.getJSONArray("ParsedResults");
            JSONObject res;
            for (int i = 0; i < parsedResults.length(); i ++) {
                res = parsedResults.getJSONObject(i);
                parsedText.add(res.getString("ParsedText"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<String> getBestGuess() {
        return parsedText;
    }

    @Override
    public String toJSONString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{[");
        for (int i = 0; i < parsedText.size(); i++) {
            builder.append(parsedText.get(i));
            if (i + 1 != parsedText.size())
                builder.append(",");
        }
        builder.append("]}");
        return builder.toString();
    }
}
