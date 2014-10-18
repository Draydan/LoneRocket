package com.example.andenginetest1;

import java.util.ArrayList;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.entity.text.*;

public class Game_Scene extends CameraScene {
	
	public class Enemy{
		public Sprite sprite;
		public float X, Y, Vx, Vy, W, H;		
	}
	
	public final AnimatedSprite hero;
	public final Sprite BGSprite;
	
	public float PulseDirection = 0.1f;	

	
	public float Vx=0, Vy=0;
	public float X=0, Y=0;
	public final double g = 300;
	public float ax=0, ay=0;
	public final int pipeSpeed = -80;
	public final int ENEMY_RADIUS = 32;
	public final int pushSpeed = -140;	
	public int touchx, touchy;
	public boolean loss = false;
	
	public double spawnTimer = 0;
	public final double spawnSpawn = 3; // seconds
	public final int pipesInRotation = 12;
	public float sizeGrowth = 0;
	public final float sizeGrowthStep = 0.025f;
	public final float sizeGrowthThreshold = 0.2f;
	
	public double deathTimer = 0;
	public final double deathTimerEndSeconds = 2; 
	
	private int GamePlayState = 0; 
	Text scoreText;	
	
	private static final int GAME_RUNNING_STATE = 2;
	private static final int GAME_STARTING_STATE = 3;
	private static final int GAME_OVERING_STATE = 4;
	
	private static final int HERO_STARTING_X = MainActivity.CAMERA_WIDTH/2;
	private static final int HERO_STARTING_Y = MainActivity.CAMERA_HEIGHT/2;
	
	private static final double PIPE_TO_SCREEN_MIN_HEIGHT = 0.15;
	private static final double PIPE_TO_SCREEN_MAX_HEIGHT = 0.3;
	
	//ArrayList<Sprite> Enemys = new  ArrayList<Sprite>();	
	ArrayList<Enemy> Enemys = new ArrayList<Enemy>();
	
	public Game_Scene()
	{
		super(MainActivity.Camera);		
		
		//final Text centerText = new Text(100, 40, this.mFont, "Hello AndEngine!\nYou can even have multilined text!", new TextOptions(HorizontalAlign.CENTER), vertexBufferObjectManager);		
		
		setBackgroundEnabled(true);
		BGSprite = new Sprite(0,  0, MainActivity.main.BG_TR, new VertexBufferObjectManager());   
		SpriteBackground sb = new SpriteBackground(BGSprite);
		this.setBackground(sb);
		
		hero = new AnimatedSprite(0, 0, MainActivity.main.Hero_TR, new VertexBufferObjectManager());
		hero.setX(HERO_STARTING_X);
		hero.setY(HERO_STARTING_Y);
		
		//hero = new Rectangle(HERO_STARTING_X ,HERO_STARTING_Y , 50, 50, new VertexBufferObjectManager()) 
		{
//			public boolean onAreaTouched(org.andengine.input.touch.TouchEvent pSceneTouchEvent, 
//					float pTouchAreaLocalX, float pTouchAreaLocalY) 
//			{
//				//MainState.ShowMainScene();
//				Vy += pushForce;
//				return true;
//			};			
		};
		
		attachChild(hero);
		hero.animate(200);
		//hero.setColor(Color.RED);		
		//registerTouchArea(hero);
	}
	
	@Override
	public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
		//Vy = pushSpeed;
		if(pSceneTouchEvent.isActionDown())
		{
			touchx = (int)pSceneTouchEvent.getX();
			touchy = (int)pSceneTouchEvent.getY();
		}
		if(pSceneTouchEvent.isActionUp())
		{
			int upx = (int)pSceneTouchEvent.getX();
			int upy = (int)pSceneTouchEvent.getY();
			Vx += (int)((upx-touchx)/5);
			Vy += (int)((upy-touchy)/5);			
		}
		return super.onSceneTouchEvent(pSceneTouchEvent);
	}
	
	public void Show ()
	{
		setVisible(true);
		setIgnoreUpdate(false);
	}

	public void Hide()
	{
		setVisible(false);
		setIgnoreUpdate(true);
	}
	
	private String GenerateScoreText()
	{
		if (MainActivity.main.score < 5)
			return "Weak! Score: " + MainActivity.main.score;
		if (MainActivity.main.score < 10)
			return "Not bad! Score: " + MainActivity.main.score;
		if (MainActivity.main.score < 15)
			return "Much Score: " + MainActivity.main.score;		
		return "Wicked Sick! Score: " + MainActivity.main.score;		
	}
	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		switch(GamePlayState)
		{
			case 0:
				GamePlayState = GAME_STARTING_STATE;
			break;
			
			case GAME_STARTING_STATE:				
				StartingPlay();
				GamePlayState = GAME_RUNNING_STATE;
			break;
			
			case GAME_RUNNING_STATE :		
			//if (hero.getWidth()<=0 || hero.getHeight()<=0 ) PulseDirection = -PulseDirection;
			//hero.setWidth(hero.getWidth() - PulseDirection);
			//hero.setHeight(hero.getHeight() - PulseDirection);
			
			//hero.setX(hero.getX()+Vx);
			
			//Vy += g * pSecondsElapsed;

			//hero.setY(hero.getY()+Math.round(Vy * pSecondsElapsed));
			//hero.setX(hero.getX()+Math.round(Vx * pSecondsElapsed));
					
			Y += Math.round(Vy * pSecondsElapsed);
			X += Math.round(Vx * pSecondsElapsed);
			
			BGSprite.setX((float)(MainActivity.CAMERA_WIDTH/2-X));
			BGSprite.setY((float)(MainActivity.CAMERA_HEIGHT/2-Y));
			
			//MainActivity.Camera.setCenter((float)X, (float)Y);
			
			
			if (hero.getY()<0) loss = true;
			
			// do spawns
			spawnTimer += pSecondsElapsed;
			if (spawnTimer >= spawnSpawn)
			{
				spawnTimer = 0;
				float sino = (float)Math.sin(Enemys.size()*2*Math.PI/pipesInRotation);
				float coso = (float)Math.sin(-Enemys.size()*2*Math.PI/pipesInRotation);
				float signo = Math.signum(sino);
				Sprite newEnemy = new Sprite(0, 0, MainActivity.main.Enemy_TR, new VertexBufferObjectManager());
				newEnemy.setX(Math.round(X + MainActivity.CAMERA_WIDTH/2));
				newEnemy.setY(Math.round(Math.random() *  MainActivity.CAMERA_HEIGHT * (1 - (sizeGrowth + PIPE_TO_SCREEN_MIN_HEIGHT + (PIPE_TO_SCREEN_MAX_HEIGHT - PIPE_TO_SCREEN_MIN_HEIGHT) * coso))));				
				newEnemy.setRotation((float)(Math.random()*360));
				//newEnemy.setHeight(Math.round(MainActivity.CAMERA_HEIGHT * (sizeGrowth + PIPE_TO_SCREEN_MIN_HEIGHT + (PIPE_TO_SCREEN_MAX_HEIGHT - PIPE_TO_SCREEN_MIN_HEIGHT) * sino)));
				
				/*Rectangle newEnemy = new Rectangle(Math.round(MainActivity.CAMERA_WIDTH * 0.9), 
					Math.round(MainActivity.CAMERA_HEIGHT * 
						(PIPE_TO_SCREEN_POSITION + PIPE_TO_SCREEN_HEIGHT * Math.sin(Enemys.size()*2*Math.PI/pipesInRotation))) , 
							50, 100, new VertexBufferObjectManager());
				*/
				attachChild(newEnemy);
				//newEnemy.setColor(Color.GREEN);
				//Rectangle newEnemy2 = new Rectangle(Math.round(MainActivity.CAMERA_WIDTH * 0.9),
				Sprite newEnemy2 = new Sprite(0, 0, MainActivity.main.Enemy_TR, new VertexBufferObjectManager());
				newEnemy2.setX(Math.round(X + MainActivity.CAMERA_WIDTH/2));
				newEnemy2.setY(Math.round(Math.random() *  MainActivity.CAMERA_HEIGHT * (1 - (sizeGrowth + PIPE_TO_SCREEN_MIN_HEIGHT + (PIPE_TO_SCREEN_MAX_HEIGHT - PIPE_TO_SCREEN_MIN_HEIGHT) * coso))));
				newEnemy2.setRotation((float)(Math.random()*360));
				//newEnemy2.setHeight(MainActivity.CAMERA_HEIGHT - newEnemy2.getY());
				
				//newEnemy2.setRotation(180);//(float) Math.PI);
				
//					Math.round(MainActivity.CAMERA_HEIGHT * 
//						(1 - PIPE_TO_SCREEN_POSITION + PIPE_TO_SCREEN_HEIGHT * Math.sin(Enemys.size()*2*Math.PI/pipesInRotation))) ,
//							50, 100, new VertexBufferObjectManager());
				attachChild(newEnemy2);
				//newEnemy2.setColor(Color.GREEN);
				Enemy E1 = new Enemy();
				Enemy E2 = new Enemy();
				E1.sprite = newEnemy;
				E2.sprite = newEnemy2;
				E1.X = newEnemy.getX();
				E1.Y = newEnemy.getY();
				E2.X = newEnemy2.getX();
				E2.Y = newEnemy2.getY();
				float speedCoef = 0.5f;
				E1.Vx = pipeSpeed*speedCoef ;
				E1.Vy = pipeSpeed*speedCoef  * (E1.Y - Y)/(E1.X - X);
				E2.Vx = pipeSpeed*speedCoef ;
				E2.Vy = pipeSpeed*speedCoef  * (E2.Y - Y)/(E2.X - X);				
				//pipeSpeed 
				Enemys.add(E1);
				Enemys.add(E2);
				
				if(sizeGrowth <= sizeGrowthThreshold) sizeGrowth += sizeGrowthStep;
			}
			//-----
			
			// move pipes
			for(int pi=0; pi<Enemys.size(); pi++)
			{
				Enemys.get(pi).X += Enemys.get(pi).Vx*pSecondsElapsed;
				Enemys.get(pi).Y += Enemys.get(pi).Vy*pSecondsElapsed;
				Sprite currEnemy = Enemys.get(pi).sprite;
				currEnemy.setX(Enemys.get(pi).X - X + MainActivity.CAMERA_WIDTH/2);
				currEnemy.setY(Enemys.get(pi).Y - Y + MainActivity.CAMERA_HEIGHT/2);
				//currEnemy.setX(currEnemy.getX() + pipeSpeed * pSecondsElapsed);
			}
			//-------------
			
			// collide enemies			
			for(int ei=0; ei<Enemys.size(); ei++)
			{
				for(int ei2=ei+1; ei2<Enemys.size(); ei2++)
				{
					Enemy e1 = Enemys.get(ei);
					Enemy e2 = Enemys.get(ei2);
					float dx = e1.X - e2.X;
					float dy = e1.Y - e2.Y;
					double dist = Math.sqrt(dx * dx + dy * dy);
					double intrusion = 2 * ENEMY_RADIUS - dist; 
					double startCollifionCoef = 0.9; 
					if ((dist < startCollifionCoef * 2 * ENEMY_RADIUS) && (dist < startCollifionCoef * 2 * ENEMY_RADIUS))
					{
						float vx = e2.Vx, vy = e2.Vy;
						e2.Vx = e1.Vx;
						e2.Vy = e1.Vy;
						e1.Vx = vx;
						e1.Vy = vy;
						
						e1.X += (2 * ENEMY_RADIUS - dist)*dx / 2;
						e1.Y += (2 * ENEMY_RADIUS - dist)*dy / 2;
						e2.X += (2 * ENEMY_RADIUS - dist)*dx / -2;
						e2.Y += (2 * ENEMY_RADIUS - dist)*dy / -2;						
					}
				}				
			}
			//
			
			// hero collision check
			for(int pi=0; pi<Enemys.size(); pi++)
			{
				Sprite currEnemy = Enemys.get(pi).sprite;
				float px = currEnemy.getX();
				float py = currEnemy.getY();
				float pw = currEnemy.getWidth();
				float ph = currEnemy.getHeight();

				float bx = hero.getX();
				float by = hero.getY();
				float bw = hero.getWidth() * 0.66f;
				float bh = hero.getHeight() * 0.66f;				
				//if(Math.abs(currEnemy.getX() - hero.getX()) < (currEnemy.getWidth() + hero.getWidth())/2 
				//		&& Math.abs(currEnemy.getY() - hero.getY()) < (currEnemy.getHeight() + hero.getHeight())/2)
				if(
						(
						( (px > bx && px < bx+bw) || (px+pw > bx && px+pw < bx+bw)) 
						&&
					( (py > by && py < by+bh) || (py+ph > by && py+ph < by+bh))
						)
						||
						(by<=0 || by >= MainActivity.CAMERA_HEIGHT)
				)
				{
					GamePlayState = GAME_OVERING_STATE;
					deathTimer = 0;
					scoreText = new Text(100, 100, MainActivity.mFont, GenerateScoreText(), null);
					scoreText.setColor(Color.YELLOW);//127/255f, 127/255f, 255/255f); //÷вет текста будет сиреневый
					this.attachChild(scoreText);
					scoreText.setColor(Color.RED);
				}
				
				if (bx > px) MainActivity.main.score = (pi+1)/2;
			}
			//---
			
			//hero.setRotation((float) ((Math.atan2(Vy, -pipeSpeed)*180)/Math.PI / 2));
			hero.setRotation((float)((Math.atan2(Vy, Vx)*180)/Math.PI)+90);			
			
			break; // end running-state
			
			case GAME_OVERING_STATE:
				if (MainActivity.main.score>MainActivity.main.hiscore) MainActivity.main.hiscore = MainActivity.main.score;
				deathTimer += pSecondsElapsed;
				if(deathTimer >= deathTimerEndSeconds) 
				{					
					this.detachChild(scoreText);
					GamePlayState = GAME_STARTING_STATE;
					EndingPlay();
					MainState.ShowMainScene();
				}
			break;			
		}
		
		//if (hero.getX() <= 0 || hero.getX() >= MainActivity.CAMERA_WIDTH ) Vx = -Vx;
		//if (hero.getY() <= 0 || hero.getY() >= MainActivity.CAMERA_HEIGHT ) Vy = -Vy;
		
		super.onManagedUpdate(pSecondsElapsed);
	}

	public void StartingPlay()
	{
		//hero.setX(HERO_STARTING_X);
		//hero.setY(HERO_STARTING_Y);
		X = HERO_STARTING_X;
		Y = HERO_STARTING_Y;
		sizeGrowth = 0;
		MainActivity.main.score = 0;
		Vx = 0;
		Vy = 0;
	}
	
	public void EndingPlay()
	{
		//MainActivity.main.score = 0;
		for(int pi = Enemys.size()-1; pi>=0; pi--) 
		{
			Sprite currEnemy = Enemys.remove(pi).sprite;			
			//currEnemy.dispose();
			detachChild(currEnemy);
			//Enemys.clear();
		}		
	}
	
}
