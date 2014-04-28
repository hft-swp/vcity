package de.hft_stuttgart.swp2.render.threads;

import de.hft_stuttgart.swp2.render.Main;

public class StartParserRunnable implements Runnable{
	private String pathToGmlFile;
	public StartParserRunnable(String pathToGmlFile){
		this.pathToGmlFile = pathToGmlFile;
	}

	@Override
	public void run() {
		Main.startParser(pathToGmlFile);
	}

}
