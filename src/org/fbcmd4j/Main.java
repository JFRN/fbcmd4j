package org.fbcmd4j;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fbcmd4j.utils.Utils;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;

// Basado en la actividad 13
public class Main {
	static final Logger logger = LogManager.getLogger(Main.class);
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "fbcmd4j.properties";

	public static void main(String[] args) {
		logger.info("Iniciando aplicacion");
		Facebook fb = null;
		Properties props = null;

		try {
			props = Utils.loadConfigFile(CONFIG_DIR, CONFIG_FILE);
			logger.info(props.toString());
		} catch (Exception ex) {
			logger.error("No pudimos cargar un archivo de configuracion. Revisalo de nuevo", ex);
		}

		int option = 1;
		try {
			Scanner scan = new Scanner(System.in);
			while (true) {
				fb = Utils.configFacebook(props);
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(
						"Cliente para interfaz de linea de comandos para manipular Facebook. Jose Rada 2018\n\n");
				stringBuilder.append("Opciones: \n");
				stringBuilder.append("(1) Ver News Feed \n");
				stringBuilder.append("(2) Ver Wall \n");
				stringBuilder.append("(3) Publicar estado \n");
				stringBuilder.append("(4) Publicar link \n");
				stringBuilder.append("(5) Salir \n");
				stringBuilder.append("\nPor favor ingrese una opción:");
				System.out.println(stringBuilder.toString());
				try {
					option = scan.nextInt();
					scan.nextLine();
					switch (option) {
					case 0:
						break;
					case 1:
						System.out.println("Mostrando NewsFeed...");
						ResponseList<Post> newsFeed = fb.getFeed();
						newsFeed.forEach(post -> Utils.printPost(post));
						askToSaveFile("NewsFeed", newsFeed, scan);
						break;
					case 2:
						System.out.println("Mostrando Wall...");
						ResponseList<Post> wall = fb.getPosts();
						wall.forEach(post -> Utils.printPost(post));
						askToSaveFile("Wall", wall, scan);
						break;
					case 3:
						System.out.println("Escribe tu estado: ");
						String estado = scan.nextLine();
						Utils.postStatus(estado, fb);
						break;
					case 4:
						System.out.println("Ingresa el link: ");
						String link = scan.nextLine();
						Utils.postLink(link, fb);
						break;
					case 5:
						System.exit(0);
						break;
					default:
						break;
					}
				} catch (InputMismatchException ex) {
					System.out.println("Ocurrió un errror, favor de revisar log.");
					logger.error("Opción inválida. %s. \n", ex.getClass());
				} catch (FacebookException ex) {
					System.out.println("Ocurrió un errror, favor de revisar log.");
					logger.error(ex.getErrorMessage());
				} catch (Exception ex) {
					System.out.println("Ocurrió un errror, favor de revisar log.");
					logger.error(ex);
				}
				System.out.println();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static void askToSaveFile(String fileName, ResponseList<Post> posts, Scanner scan) {
		System.out.println("Guardar resultados en un archivo de texto? Si/No");
		String option = scan.nextLine();

		if (option.contains("Si".toUpperCase())) {
			List<Post> ps = new ArrayList<>();
			int n = 0;

			while (n <= 0) {
				try {
					System.out.println("Cuántos posts deseas guardar?");
					n = Integer.parseInt(scan.nextLine());

					if (n <= 0) {
						System.out.println("Favor de ingresar un número válido");
					} else {
						for (int i = 0; i < n; i++) {
							if (i > posts.size() - 1)
								break;
							ps.add(posts.get(i));
						}
					}
				} catch (NumberFormatException e) {
					logger.error(e);
				}
			}

			Utils.savePostsToFile(fileName, ps);
		}
	}
}