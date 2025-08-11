package com.chisom.authservice.service;

public abstract class TestProperties {
    public static final String CLIENT_SECRET = "f1fLjcTMkh-WsRP0efBNpQGQB3191B-ihmmYTyOJnstESYGuE1CZuGN9OOFUvup";
    public static final String CLIENT_ID = "ARwdsvCGmeerDq9xeQg9MtGAnky4eHhL";
    public static final String SECRET = "AVvFRe8GVPqo90Rcugm4qtudPoJM3+jUBz/9Hrx99c3LqKYt6um/Y72b3eRztdD4x7RVBigAuXLh31TdwMVvSQDWMcZ5";
    public static final String AES_KEY = """
            {
                  "primaryKeyId": 1539655151,
                  "key": [
                    {
                      "keyData": {
                        "typeUrl": "type.googleapis.com/google.crypto.tink.AesGcmKey",
                        "value": "GiCRTrYRClzNggHbknQGlZxlStAfVRwmWe38wqzIYGWBvw==",
                        "keyMaterialType": "SYMMETRIC"
                      },
                      "status": "ENABLED",
                      "keyId": 1539655151,
                      "outputPrefixType": "TINK"
                    }
                  ]
                }
            """;
}
