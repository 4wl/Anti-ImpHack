package mod.imphack.module;

import mod.imphack.event.events.ImpHackEventRender;
import mod.imphack.module.modules.client.ClickGui;
import mod.imphack.module.modules.client.DiscordRPC;
import mod.imphack.module.modules.combat.*;
import mod.imphack.module.modules.hud.Hud;
import mod.imphack.module.modules.movement.*;
import mod.imphack.module.modules.player.*;
import mod.imphack.module.modules.render.*;
import mod.imphack.module.modules.utilities.*;
import mod.imphack.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	public static final Minecraft mc = Minecraft.getMinecraft();

	public final ArrayList<Module> modules;

	public ModuleManager() {
		modules = new ArrayList<>();

		// client
		addModule(new ClickGui());
		addModule(new Hud());
		addModule(new DiscordRPC());

		// combat
		addModule(new KillAura()); // TODO Add Switch and Sword only
		addModule(new CrystalAura());// TODO BROKEN WILL FIX TMRW
		addModule(new Surround());// TODO fix surround its broken
		addModule(new AutoTotem());
		addModule(new Anchor());
		addModule(new Criticals());
		//addModule(new SelfFill());


		// movement
		addModule(new Speed());
		addModule(new Flight());
		addModule(new Jesus());
		addModule(new AutoWalk());
		addModule(new EntityRide());
		addModule(new NoSlow());
		addModule(new Sprint());
		// TODO fix player push
		addModule(new Velocity());
		addModule(new ElytraFlight());
		addModule(new BoatFly());
		addModule(new Parkour());

		// player
		// TODO nohunger
		addModule(new Scaffold());
		addModule(new AutoEat());
		addModule(new NoHunger());
		addModule(new Disabler());
		addModule(new FakePlayer());
		addModule(new XCarry());

		// hud
		//GOTO Main

		// render
		addModule(new EntityTracers());
		addModule(new FullBright());
		addModule(new Freecam());// TODO fix entity dismounting with shift in freecam/make it baritone
		addModule(new BlockOverlay());// compatible, fix character skin, fix desync and glitch on disable
		addModule(new Nametags());
		addModule(new ExtraTab());
		addModule(new HoleEsp());
		addModule(new Esp());
		addModule(new Esp2dHelper());

		//addModule(new NoRender());
		// TODO norender
		// TODO newchunks
		// TODO camera clip
		// TODO camera distance
		addModule(new Search());
		// TODO map (generate chunks from seed to show for not yet loaded chunks)
		// consider making minimap addition to hud module
		// TODO seedoverlay (generate chunks from seed to see player activity)

		// utilities
		// TODO fix autofish rod disappearing/items being held in inventory for offhand
		// mending repair, also add timer so not reeling and casting rapidly
		addModule(new AutoFish());
		addModule(new Reconnect());
		addModule(new Spammer());
		// TODO fix concretebot to work on 2b2t
		addModule(new ConcreteBot());
		// this module is just an experiment and is not used in the current version of
		// the client
		// addModule(new NoteBot());
		addModule(new LSD());
                // TODO add visual range player notifier
	}

	public void addModule(Module m) {
		this.modules.add(m);
	}

	public Module getModule(String name) {
		for (Module m : this.modules) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}

	public List<Module> getModuleList() {
		new ModuleManager();
		return this.modules;
	}

	public List<Module> getModulesByCategory(Category c) {
		List<Module> modules = new ArrayList<>();

		for (Module m : this.modules) {
			if (m.getCategory() == c)
				modules.add(m);
		}
		return modules;
	}

	public void update() {
		for (Module module : modules) {
			if (module.toggled) {
				module.onUpdate();
			}
		}
	}

	public void render(RenderWorldLastEvent event) {
		mc.profiler.startSection("ImpHack Revised");
		mc.profiler.startSection("setup");

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.disableDepth();

		GlStateManager.glLineWidth(1f);

		Vec3d pos = get_interpolated_pos(mc.player, event.getPartialTicks());

		ImpHackEventRender event_render = new ImpHackEventRender(RenderUtil.INSTANCE, pos, 0);

		event_render.reset_translation();

		mc.profiler.endSection();

		for (Module m : getModuleList()) {
			if (m.toggled) {
				mc.profiler.startSection(m.name);

				m.render(event_render);

				mc.profiler.endSection();
			}
		}

		mc.profiler.startSection("release");

		GlStateManager.glLineWidth(1f);

		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.enableCull();

		RenderUtil.release_gl();

		mc.profiler.endSection();
		mc.profiler.endSection();
	}

	public void render() {
		for (Module m : getModuleList()) {
			if (m.toggled) {
				m.render();
			}
		}
	}

	public Vec3d get_interpolated_pos(Entity entity, double ticks) {
		return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ)
				.add(process(entity, ticks, ticks, ticks)); // x, y, z.
	}

	public Vec3d process(Entity entity, double x, double y, double z) {
		return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y,
				(entity.posZ - entity.lastTickPosZ) * z);
	}
	
	  public void onClientTick(TickEvent.ClientTickEvent event) {
	        for (Module m : getModuleList())  {
	        	if(m.toggled) {
	            m.onClientTick(event);
	        	}
	        	
	        }
	    }
}
