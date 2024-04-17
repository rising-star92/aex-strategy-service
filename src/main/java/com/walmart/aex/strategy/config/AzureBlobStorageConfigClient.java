package com.walmart.aex.strategy.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.walmart.aex.strategy.properties.CredProperties;
import net.minidev.json.parser.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

import static com.walmart.aex.strategy.util.Constant.*;

@Configuration
public class AzureBlobStorageConfigClient {

  private final CredProperties credProperties;

  public AzureBlobStorageConfigClient(CredProperties credProperties) {
    this.credProperties = credProperties;
  }

  @Bean
  public BlobServiceClient blobServiceClient() throws IOException {
      return new BlobServiceClientBuilder().endpoint(credProperties.getSasURL()).sasToken(credProperties.getSasToken())
          .buildClient();
  }

  @Bean
  public BlobContainerClient blobContainerClient(final BlobServiceClient blobServiceClient) throws IOException {
    final String containerName = credProperties.getContainerName();
    return blobServiceClient.getBlobContainerClient(containerName);
  }

}
