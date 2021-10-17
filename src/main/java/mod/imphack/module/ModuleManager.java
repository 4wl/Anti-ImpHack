package mod.imphack.module;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import mod.imphack.event.events.ImpHackEventRender;
import mod.imphack.module.modules.client.ClickGui;
import mod.imphack.module.modules.client.DiscordRPC;
import mod.imphack.module.modules.combat.Anchor;
import mod.imphack.module.modules.combat.AutoTotem;
import mod.imphack.module.modules.combat.Criticals;
import mod.imphack.module.modules.combat.CrystalAura;
import mod.imphack.module.modules.combat.KillAura;
import mod.imphack.module.modules.combat.Surround;
import mod.imphack.module.modules.hud.Hud;
import mod.imphack.module.modules.hud.Welcome;
import mod.imphack.module.modules.movement.AutoWalk;
import mod.imphack.module.modules.movement.BoatFly;
import mod.imphack.module.modules.movement.ElytraFlight;
import mod.imphack.module.modules.movement.EntityRide;
import mod.imphack.module.modules.movement.Flight;
import mod.imphack.module.modules.movement.Jesus;
import mod.imphack.module.modules.movement.Parkour;
import mod.imphack.module.modules.movement.Speed;
import mod.imphack.module.modules.movement.Sprint;
import mod.imphack.module.modules.movement.Velocity;
import mod.imphack.module.modules.player.AutoEat;
import mod.imphack.module.modules.player.Disabler;
import mod.imphack.module.modules.player.FakePlayer;
import mod.imphack.module.modules.player.Scaffold;
import mod.imphack.module.modules.player.XCarry;
import mod.imphack.module.modules.render.EntityTracers;
import mod.imphack.module.modules.render.ExtraTab;
import mod.imphack.module.modules.render.Freecam;
import mod.imphack.module.modules.render.FullBright;
import mod.imphack.module.modules.render.LSD;
import mod.imphack.module.modules.render.Nametags;
import mod.imphack.module.modules.render.Search;
import mod.imphack.module.modules.utilities.AutoFish;
import mod.imphack.module.modules.utilities.ConcreteBot;
import mod.imphack.module.modules.utilities.NoHunger;
import mod.imphack.module.modules.utilities.Reconnect;
import mod.imphack.module.modules.utilities.Spammer;
import mod.imphack.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ModuleManager {
	public static Minecraft mc = Minecraft.getMinecraft();

	public ArrayList<Module> modules;

	public ModuleManager() {
		modules = new ArrayList<Module>();
		modules.clear();

		// client
		addModule(new ClickGui());
		addModule(new Hud());
		addModule(new DiscordRPC());

		// combat
		addModule(new KillAura());
		addModule(new CrystalAura());// TODO fix
		addModule(new Surround());// TODO fix surround its broken
		addModule(new AutoTotem());
		addModule(new Anchor());
		addModule(new Criticals());

		// movement
		addModule(new Speed());
		addModule(new Flight());
		addModule(new Jesus());
		addModule(new AutoWalk());
		addModule(new EntityRide());
		addModule(new Sprint());
		// TODO fix player push in velocity
		addModule(new Velocity());
		addModule(new ElytraFlight()); // Done elytrafly By John Xina
		addModule(new BoatFly());
		addModule(new Parkour());

		// player
		// TODO nohunger is broke
		addModule(new Scaffold());
		addModule(new AutoEat());
		addModule(new NoHunger());
		addModule(new Disabler());
		addModule(new FakePlayer());
		addModule(new XCarry());

		// hud

		addModule(new Welcome());

		// render
		addModule(new EntityTracers());
		addModule(new FullBright());
		addModule(new Freecam());// TODO fix entity dismounting with shift in freecam/make it baritone
									// compatible, fix character skin
		addModule(new Nametags());
		addModule(new ExtraTab());
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
		List<Module> modules = new ArrayList<Module>();

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

		ImpHackEventRender event_render = new ImpHackEventRender(RenderUtil.INSTANCE, pos);

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
}
