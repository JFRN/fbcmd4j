package org.fbcmd4j.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.auth.AccessToken;

public class Utils {
	private static final Logger logger = LogManager.getLogger(Utils.class);

	public static Properties loadConfigFile(String folderName, String fileName) throws IOException {
		Properties props = new Properties();
		Path configFile = Paths.get(folderName, fileName);
		props.load(Files.newInputStream(configFile));
		BiConsumer<Object, Object> emptyProperty = (k, v) -> {
			if (((String) v).isEmpty()) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("La propiedad '");
				stringBuilder.append(k);
				stringBuilder.append("' esta vacía");
				logger.info(stringBuilder.toString());
			}
		};
		props.forEach(emptyProperty);
		return props;
	}

	public static void saveProperties(String folderName, String fileName, Properties props) throws IOException {
		Path configFile = Paths.get(folderName, fileName);
		String string = "Generado por org.fbcmd4j.configTokens";
		props.store(Files.newOutputStream(configFile), string);
	}

	public static Facebook configFacebook(Properties props) {
		Facebook fb = new FacebookFactory().getInstance();
		fb.setOAuthAppId(props.getProperty("oauth.appId"), props.getProperty("oauth.appSecret"));
		fb.setOAuthPermissions(props.getProperty("oauth.permissions"));
		if (props.getProperty("oauth.accessToken") != null)
			fb.setOAuthAccessToken(new AccessToken(props.getProperty("oauth.accessToken"), null));
		return fb;
	}

	public static void printPost(Post p) {
		if (p.getStory() != null) {
			StringBuilder stringBuilder2 = new StringBuilder();
			stringBuilder2.append("Story: ");
			stringBuilder2.append(p.getStory());
			System.out.println(stringBuilder2.toString());
		}
		if (p.getMessage() != null) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Mensaje: ");
			stringBuilder.append(p.getMessage());
			System.out.println(stringBuilder.toString());
		}
		String string = "--------------------------------";
		System.out.println(string);
	}

	public static void postStatus(String msg, Facebook fb) {
		try {
			fb.postStatusMessage(msg);
		} catch (FacebookException e) {
			logger.error(e);
		}
	}

	public static void postLink(String link, Facebook fb) {
		try {
			fb.postLink(new URL(link));
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (FacebookException e) {
			logger.error(e);
		}
	}

	public static String savePostsToFile(String fileName, List<Post> posts) {
		StringBuilder stringBuilder2 = new StringBuilder();
		stringBuilder2.append(fileName);
		stringBuilder2.append(".txt");
		File file = new File(stringBuilder2.toString());

		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(file);

			for (Post p : posts) {
				String msg = "";
				if (p.getStory() != null) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("Story: ");
					stringBuilder.append(p.getStory());
					stringBuilder.append("\n");
					msg += stringBuilder.toString();
				}
				if (p.getMessage() != null) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("Mensaje: ");
					stringBuilder.append(p.getMessage());
					stringBuilder.append("\n");
					msg += stringBuilder.toString();
				}
				String string = "--------------------------------\n";
				msg += string;
				fos.write(msg.getBytes());
			}
			fos.close();

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Posts guardados en el archivo '");
			stringBuilder.append(file.getName());
			stringBuilder.append("'.");
			logger.info(stringBuilder.toString());
			StringBuilder stringBuilder3 = new StringBuilder();
			stringBuilder3.append("Posts guardados exitosamente en '");
			stringBuilder3.append(file.getName());
			stringBuilder3.append("'.");
			System.out.println(stringBuilder3.toString());
		} catch (IOException e) {
			logger.error(e);
		}

		return file.getName();
	}
}