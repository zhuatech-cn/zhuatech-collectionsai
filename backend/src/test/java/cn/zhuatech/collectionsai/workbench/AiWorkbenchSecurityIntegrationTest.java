/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.collectionsai.workbench;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cn.zhuatech.collectionsai.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@SpringBootTest
@AutoConfigureMockMvc
class AiWorkbenchSecurityIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired JwtService jwt;

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void securityFilterRejectsAnonymous() throws Exception {
    mvc.perform(
            post("/api/workbench/receivable-aging")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"asOf\":\"2026-10-23\",\"invoices\":[{\"id\":\"I1\",\"dueDate\":\"2026-10-01\",\"amount\":1000,\"applied\":200,\"disputed\":false},{\"id\":\"I2\",\"dueDate\":\"2026-11-01\",\"amount\":500,\"applied\":0,\"disputed\":true}]}"))
        .andExpect(status().isUnauthorized());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void securityFilterRejectsInvalidBearer() throws Exception {
    mvc.perform(
            post("/api/workbench/receivable-aging")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"asOf\":\"2026-10-23\",\"invoices\":[{\"id\":\"I1\",\"dueDate\":\"2026-10-01\",\"amount\":1000,\"applied\":200,\"disputed\":false},{\"id\":\"I2\",\"dueDate\":\"2026-11-01\",\"amount\":500,\"applied\":0,\"disputed\":true}]}"))
        .andExpect(status().isUnauthorized());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void authenticatedCallerCanCompute() throws Exception {
    mvc.perform(
            post("/api/workbench/receivable-aging")
                .header("Authorization", "Bearer " + jwt.generate("operator"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"asOf\":\"2026-10-23\",\"invoices\":[{\"id\":\"I1\",\"dueDate\":\"2026-10-01\",\"amount\":1000,\"applied\":200,\"disputed\":false},{\"id\":\"I2\",\"dueDate\":\"2026-11-01\",\"amount\":500,\"applied\":0,\"disputed\":true}]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void authenticatedCallerGetsValidationError() throws Exception {
    mvc.perform(
            post("/api/workbench/receivable-aging")
                .header("Authorization", "Bearer " + jwt.generate("operator"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }
}
