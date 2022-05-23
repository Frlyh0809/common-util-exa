/*
 * https://nftscan.com/
 * Copyright © 2022  All rights reserved.
 */
package com.frlyh.exa.tests;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author frlyh
 * @version 1.0: AbbJson.java, v 0.1 2022/05/23 14:05 PM  frlyh Exp $
 */
@SpringBootTest
@Slf4j
public class AbbJsonTests {



    @Test
    public void doTest_1(){
        String json="{\n" +
                "    \"erc\": \"erc1155\",\n" +
                "    \"page_index\": 1,\n" +
                "    \"page_size\": 10,\n" +
                "    \"user_address\": \"0xffc2634c105e1ec63cc862d9760e07f41dc690e6\"\n" +
                "}";
        AddressAndErcTypeDTO req = JSONObject.parseObject(json, AddressAndErcTypeDTO.class);
        log.info("req1 = [{}]",req);
        log.info("req2 = [{}]", JSONObject.toJSONString(req));
    }

    @Data
    static class AddressAndErcTypeDTO  {

        private String user_address;
        private String erc;
        private Integer page_index = 1;
        private Integer page_size = 10;
        private Integer ercType;

        /**
         * 初始参数转换小写
         */
        public void toLowerCaseInit(){

            user_address=user_address.toLowerCase().trim();
            erc=erc.toLowerCase().trim();
        }

    }

}
