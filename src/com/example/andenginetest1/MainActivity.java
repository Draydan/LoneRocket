package com.example.andenginetest1;

import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.engine.camera.*;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.font.*;
 
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.KeyEvent;
 
public class MainActivity extends SimpleBaseGameActivity{
	
	public TiledTextureRegion Hero_TR;
	public TextureRegion Enemy_TR;
	public TextureRegion BG_TR;
	public static Font font_BosaNova54;
	public static Font font_BosaNova22;
	public static Font mFont;
	
	public static final int CAMERA_WIDTH = 1024;
	public static final int CAMERA_HEIGHT = 600;
	
	public int score = 0;
	public int hiscore = 0;	

	public static MainState MainState;
	public static Camera Camera;
	private boolean GameLoaded = false;
	public static MainActivity main;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		main = this;
		Camera = new Camera(0,0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions opt = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR , 
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), Camera);
		opt.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		return opt;
	}

	
	@Override
	protected void onCreateResources() {
		 /**
         * Указываем путь до графики. В данном случае графика будет загружаться из папки assets/gfx/
         */
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
 
        /**
         * Создаем Атлас, в который будем загружать графику.
         * TextureOptions - задаёт режим вывода графики на экран.
         * Если вы планируете масштабировать спрайты, изменять прозрачность и спрайты
         * имеют изначально размыте края, то лучше ставить BILINEAR_PREMULTIPLYALPHA
         * Тут нужно эскпериментировать
         * Бывает:
         *      NEAREST
         *      NEAREST_PREMULTIPLYALPHA
         *      BILINEAR
         *      BILINEAR_PREMULTIPLYALPHA
         *      и др.
         */
        TextureManager tm = mEngine.getTextureManager();
        BitmapTextureAtlas TextureHero = new BitmapTextureAtlas(tm, 128, 128, TextureOptions.NEAREST_PREMULTIPLYALPHA);
        //BitmapTextureAtlas TextureHero = new BitmapTextureAtlas(tm, 128, 64, TextureOptions.NEAREST_PREMULTIPLYALPHA);
        BitmapTextureAtlas TextureEnemy = new BitmapTextureAtlas(tm, 64, 64, TextureOptions.NEAREST_PREMULTIPLYALPHA);
        //BitmapTextureAtlas TextureFon = new BitmapTextureAtlas(tm,  1024, 512, TextureOptions.NEAREST_PREMULTIPLYALPHA);
        BitmapTextureAtlas TextureFon = new BitmapTextureAtlas(tm,  1920, 1200, TextureOptions.NEAREST_PREMULTIPLYALPHA);

        /**
         * Создаем регион (область) для спрайта в этом атласе.
         * Нужно указать координаты региона в Атласе (0,0 в данном случае)
         */
        
        Hero_TR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset (TextureHero, this, "rocket2.png", 0, 0, 2, 1);
        //Hero_TR = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset (TextureHero, this, "bird_winged_anim_clear_bg.png", 0, 0, 2, 1);
        Enemy_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset (TextureEnemy, this, "asteroid1.png", 0, 0);
        BG_TR = BitmapTextureAtlasTextureRegionFactory.createFromAsset (TextureFon, this, "pinkspacehd1.jpg", 0, 0);
 
        /**
         * Теперь всё готово. Загружаем Атлас
         */
        tm.loadTexture(TextureHero);
        tm.loadTexture(TextureEnemy);
        tm.loadTexture(TextureFon);
        
        
		mFont = FontFactory.create(this.getFontManager(), 
				this.getTextureManager(), 256, 256, 
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, Color.WHITE_ABGR_PACKED_INT);
		mFont.load();  
		
        /*
        FontManager fm = mEngine.getFontManager();
        AssetManager am = this.getAssets(); 
        
        //Загружаю шрифт BosaNova размером 22 пикселя
        BitmapTextureAtlas font_BosaNova22_Texture = new BitmapTextureAtlas(tm, 512, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        font_BosaNova22 = FontFactory.createFromAsset(fm, font_BosaNova22_Texture, am, "fonts/bosanova.ttf", 22, false, Color.WHITE);
        		//(fm, tm, 512, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA, am, "fonts/bosanova.ttf", 22, false, android.graphics.Color.WHITE);

        //(fm, tm, font_BosaNova22_Texture, this, "fonts/bosanova.ttf", 22, true, Color.WHITE);
         
        //Загружаю шрифт BosaNova размером 54 пикселя
        BitmapTextureAtlas font_BosaNova54_Texture = new BitmapTextureAtlas(tm, 512, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        font_BosaNova54 = FontFactory.createFromAsset        		(fm, tm, 512, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA, am, "fonts/bosanova.ttf", 54, false, android.graphics.Color.WHITE); 
        		//FontFactory.createFromAsset(fm, font_BosaNova54_Texture, this, "fonts/bosanova.ttf", 54, true, Color.WHITE);
         
        tm.loadTexture(font_BosaNova22_Texture);
        tm.loadTexture(font_BosaNova54_Texture);
        fm.loadFonts(font_BosaNova22, font_BosaNova54);
        */        
	}

	@Override
	protected Scene onCreateScene() {
		MainState = new MainState();
		GameLoaded = true;
		return MainState;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(!GameLoaded) return true;
			if(MainState != null && GameLoaded)
			{
				MainState.KeyPressed(keyCode, event);				
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}