package org.hac.codewsp.model;

// create record class for Response with fields statusCode and body


public record UPFileResponse(String filename) {

 }


/*
 * {
    "cookies" : ["cookie1", "cookie2"],
    "isBase64Encoded": true|false,
    "statusCode": httpStatusCode,
    "headers": { "headername": "headervalue", ... },
    "body": "Hello from Lambda!"
}      
 */