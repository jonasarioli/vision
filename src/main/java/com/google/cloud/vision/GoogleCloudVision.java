package com.google.cloud.vision;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

import com.google.cloud.vision.spi.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;

public class GoogleCloudVision {

	public static void processImage(File file, JTextArea log) throws IOException {
		// Instantiates a client
		ImageAnnotatorClient vision = ImageAnnotatorClient.create();

		// Reads the image file into memory
		byte[] data;
		data = Files.readAllBytes(file.toPath());

		ByteString imgBytes = ByteString.copyFrom(data);

		// Builds the image annotation request
		List<AnnotateImageRequest> requests = new ArrayList<>();
		Image img = Image.newBuilder().setContent(imgBytes).build();
		Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		// Performs label detection on the image file
		BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
		List<AnnotateImageResponse> responses = response.getResponsesList();

		StringBuilder description = new StringBuilder();
		
		for (AnnotateImageResponse res : responses) {
			if (res.hasError()) {
				System.out.printf("Error: %s\n", res.getError().getMessage());
				return;
			}
			
			for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
				annotation.getAllFields().forEach((k, v) -> description.append(String.format("%s : %s\n", k, v.toString())));
			}
		}
		
		log.append(description.toString());
	}
}
