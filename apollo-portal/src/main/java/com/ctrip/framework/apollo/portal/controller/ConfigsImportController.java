/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ctrip.framework.apollo.portal.controller;

import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.ctrip.framework.apollo.portal.service.ConfigsImportService;
import com.ctrip.framework.apollo.portal.util.ConfigFileUtils;
import java.io.IOException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Import the configs from file.
 * First version: move code from {@link ConfigsExportController}
 * @author wxq
 */
@RestController
public class ConfigsImportController {

  private final ConfigsImportService configsImportService;

  public ConfigsImportController(
      final ConfigsImportService configsImportService
  ) {
    this.configsImportService = configsImportService;
  }

  /**
   * copy from old {@link ConfigsExportController}.
   * @param file Yml file's name must ends with {@code .yml}.
   *             Properties file's name must ends with {@code .properties}.
   *             etc.
   * @throws IOException
   */
  @PreAuthorize(value = "@permissionValidator.hasModifyNamespacePermission(#appId, #namespaceName, #env)")
  @PostMapping("/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/items/import")
  public void importConfigFile(@PathVariable String appId, @PathVariable String env,
      @PathVariable String clusterName, @PathVariable String namespaceName,
      @RequestParam("file") MultipartFile file) throws IOException {
    // check file
    ConfigFileUtils.check(file);
    final String format = ConfigFileUtils.getFormat(file.getOriginalFilename());
    final String standardFilename = ConfigFileUtils.toFilename(appId, clusterName, namespaceName, ConfigFileFormat.fromString(format));
    configsImportService.importOneConfigFromFile(env, standardFilename, file.getInputStream());
  }
}
