package com.fajar.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fajar.dto.Physical;
import com.fajar.dto.Entity;
import com.fajar.parameter.EntityParameter;
import com.fajar.util.JSONUtil;

@Service
public class LayoutService {
	Logger log = LoggerFactory.getLogger(LayoutService.class);

	private static List<Entity> layouts = new ArrayList<>();

	public static void main(String[] ddf) {
		new LayoutService().load();
		System.out.println(JSONUtil.listToJson(layouts));
	}

	public LayoutService() {
		load();
	}

	public void load() {
		try {
			URL path = getClass().getResource("layout1.png");
			BufferedImage layout1 = ImageIO.read(path);
			createLayout(layout1);
			log.info("------------------LOADED LAYOUT: {}", layouts);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createLayout(BufferedImage denah) {

		int width = denah.getWidth();
		int height = denah.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = denah.getRGB(x, y);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;

				if (red == 255 && green == 0 && blue == 0) {
					int xPos = x * 60 + 30;
					int yPos = y * 40+ 30;
					Entity layoutEntity = new Entity(new Random().nextInt(101010) + 1,
							"layout_" + xPos + "-" + yPos, new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setY(yPos);
					entity.setLayout(true);
					entity.setRole(EntityParameter.ROLE_LAYOUT_1);
					layoutEntity.setPhysical(entity);
					layouts.add(layoutEntity);
				}
				if (red == 0 && green == 0 && blue == 0) {
					int xPos = x * 60 + 30;
					int yPos = y * 40+ 30;
					Entity layoutEntity = new Entity(new Random().nextInt(101010) + 1,
							"layout_" + xPos + "-" + yPos, new Date());
					Physical entity = new Physical();
					entity.setX(xPos);
					entity.setLayout(true);
					entity.setY(yPos);
					entity.setRole(EntityParameter.ROLE_LAYOUT_1);
					layoutEntity.setPhysical(entity);
					layouts.add(layoutEntity);
				}
			}
		}

	}

	public List<Entity> getLayouts() {
		return layouts;
	}

	public void setLayouts(List<Entity> layouts) {
		LayoutService.layouts = layouts;
	}

}
