package test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import box2dLight.DirectionalLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2DTest extends ApplicationAdapter {
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private RayHandler handler;
	
	public void create() {		
		Box2D.init();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 40, 20);
		
		world = new World(new Vector2(0, -10), true);
		
		debugRenderer = new Box2DDebugRenderer();
		
		createBall(world);
		createGround(world);
		
		handler = new RayHandler(world);
		//handler.setShadows(false);
		
		new PointLight(handler, 500, new Color(1, 1, 1, 1), 16, 20, 10);
		//new DirectionalLight(handler, 500, new Color(1, 1, 1, 1), -90);
	}
	
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		handler.setCombinedMatrix(camera);
		handler.updateAndRender();
		
		debugRenderer.render(world, camera.combined);
		
		doPhysicsStep(Gdx.graphics.getDeltaTime());
	}
	
	private float accumulator;
	private static final float TIME_STEP = 1 / 60f;
	private static final int VELOCITY_ITERATIONS = 6;
	private static final int POSITION_ITERATIONS = 2;
	
	private void doPhysicsStep(float dt) {
		accumulator += Math.min(dt, 0.25f);
		
		while (accumulator >= TIME_STEP) {
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			
			accumulator -= TIME_STEP;
		}
	}
	
	private static void createBall(World world) {
		BodyDef ballDef = new BodyDef();
		ballDef.type = BodyType.DynamicBody;
		ballDef.position.set(20, 20);
		
		Body body = world.createBody(ballDef);
		
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(4);

		FixtureDef ballFixDef = new FixtureDef();
		ballFixDef.shape = circleShape;
		ballFixDef.density = 0.5f;
		ballFixDef.friction = 0.4f;
		ballFixDef.restitution = 1f;
		
		body.createFixture(ballFixDef);
		
		circleShape.dispose();
	}
	
	private static void createGround(World world) {
		BodyDef groundDef = new BodyDef();
		groundDef.position.set(20, 0.5f);
		
		Body ground = world.createBody(groundDef);
		
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(20, 0.5f);
		
		ground.createFixture(groundBox, 0.0f);
		
		groundBox.dispose();
	}
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
		handler.dispose();
	}
}
