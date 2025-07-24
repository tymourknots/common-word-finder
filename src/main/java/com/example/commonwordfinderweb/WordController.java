package com.example.commonwordfinderweb;

import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;


@RestController
@RequestMapping("/api")
public class WordController {

    @PostMapping("/process")
    public Map<String, Object> processText(@RequestBody WordRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (request.getText() == null || request.getText().isEmpty()){
            response.put("error", "Input text cannot be empty.");
            return response;
        }

        if (request.getText().length() > 100000){
            response.put("error", "Input text is too large.");
            return response;
        }

        try{
            long start = System.nanoTime();
            Map<String, Object> resultData = CommonWordFinder.getResultsFromText(
                request.getText(), 
                request.getStructure(),
                request.getLimit()
            );
            long end = System.nanoTime();

            double runtimeMs = (end - start) / 1_000_000.0;
            resultData.put("runtime", runtimeMs);

            return resultData;
        } catch (Exception e){
            response.put("error", "An internal error occurred. Please try again.");
            return response;
        }
    }
}   
