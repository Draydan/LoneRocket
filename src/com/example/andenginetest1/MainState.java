package com.example.andenginetest1;

import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import android.view.KeyEvent;

public class MainState extends Scene {
	
	public static MainMenu_Scene MainMenu_Scene = new MainMenu_Scene();
	public static Game_Scene Game_Scene = new Game_Scene();
	
	private static int GameState;
	
	private static final int MAIN_MENU_STATE = 0;
	private static final int SELECT_LEVELS_STATE = 1;
	private static final int GAME_RUNNING_STATE = 2;
	//private static final int GAME_STARTING_STATE = 3;
	//private static final int GAME_OVERING_STATE = 4;
	
	public MainState()
	{
		attachChild(MainMenu_Scene);
		attachChild(Game_Scene);
		ShowMainScene();
	}
	
	public static void ShowMainScene()
	{
		MainMenu_Scene.Show();
		Game_Scene.Hide();
		GameState = MAIN_MENU_STATE ;
	}

	public static void HideMainScene()
	{
		MainMenu_Scene.Hide();
		Game_Scene.Show();
		GameState = GAME_RUNNING_STATE ;	
	}	
	
	@Override
	public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
		switch(GameState)
		{
			case MAIN_MENU_STATE:
				MainMenu_Scene.onSceneTouchEvent(pSceneTouchEvent);
				break;
			case GAME_RUNNING_STATE :
				Game_Scene.onSceneTouchEvent(pSceneTouchEvent);				
				break;			
		}
		return super.onSceneTouchEvent(pSceneTouchEvent);
	}

	public void KeyPressed(int keyCode, KeyEvent event) {
		switch(GameState)
		{
			case MAIN_MENU_STATE:
				MainActivity.main.onDestroy();				
				break;
			case GAME_RUNNING_STATE :
				ShowMainScene();				
				break;
			
		}		
	}
}
