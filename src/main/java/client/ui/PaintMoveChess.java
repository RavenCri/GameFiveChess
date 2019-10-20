package com.raven.main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
/**
 * 
 * @author Raven
 * @date 下午5:57:23
 * @version
 * 	为了让动画效果能够更流程，该线程就是解决该问题的
 */
public class PaintMoveChess extends Thread{
	BufferedImage bf;
	Graphics g; 
	Graphics2D g2;
	GamePlane gamePlane;
		public PaintMoveChess(){
			
		}
	
		public PaintMoveChess(BufferedImage bf, Graphics g, Graphics2D g2, GamePlane gamePlane) {
			
			this.bf = bf;
			this.g = g;
			this.g2 = g2;
			this.gamePlane = gamePlane;
		}

		@Override
		public void run() {
			if(gamePlane.MouseAtChess) {
				if(!gamePlane.isme) {
					g2.drawImage(gamePlane.stopImage.getImage(), (int)gamePlane.mouseMove.getX()-25,(int)gamePlane.mouseMove.getY()-25,45,45,gamePlane.stopImage.getImageObserver());
					
				}else {
					if(gamePlane.MyChessColor.equals("white")) {
						g2.drawImage(gamePlane.bqizi, (int)gamePlane.mouseMove.getX()-40,(int)gamePlane.mouseMove.getY()-20,60,60,gamePlane);
						
						
					}else {
						g2.drawImage(gamePlane.hqizi, (int)gamePlane.mouseMove.getX()-30,(int)gamePlane.mouseMove.getY()-30,60,60,gamePlane);
					
					}
				}

			}
			if(gamePlane.musicing) {
				
				g2.drawImage(GamePlane.StopBG.getImage(), 1200, 30,50,50, gamePlane);
			}else {
				
				g2.drawImage(GamePlane.beginBG.getImage(), 1200, 30,50,50, gamePlane);
			}
			g.drawImage(bf,0,0,gamePlane);
			
		}
}
