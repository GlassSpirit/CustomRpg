package ru.glassspirit.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.SplashProgress;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.Drawable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.glassspirit.eclipsis.client.ClientProxy;
import ru.glassspirit.eclipsis.client.gui.Texture;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
@Mixin(SplashProgress.class)
public abstract class MixinSplashProgress {

    @Shadow(remap = false)
    @Final
    static Semaphore mutex;
    @Shadow(remap = false)
    static boolean isDisplayVSyncForced;
    @Shadow(remap = false)
    private static volatile boolean done;
    @Shadow(remap = false)
    private static Thread thread;
    @Shadow(remap = false)
    @Final
    private static Lock lock;
    @Shadow(remap = false)
    private static boolean enabled;
    @Shadow(remap = false)
    private static Drawable d;
    @Shadow(remap = false)
    @Final
    private static int TIMING_FRAME_COUNT;
    @Shadow(remap = false)
    private static volatile boolean pause;
    @Shadow(remap = false)
    @Final
    private static int TIMING_FRAME_THRESHOLD;

    @Shadow(remap = false)
    private static boolean disableSplash(Exception e) {
        return false;
    }

    @Shadow(remap = false)
    private static void checkThreadState() {
    }

    @Inject(method = "start", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;setUncaughtExceptionHandler(Ljava/lang/Thread$UncaughtExceptionHandler;)V"), remap = false)
    private static void setNewThread(CallbackInfo ci) {
        thread = new Thread(MixinSplashProgress::drawSplashScreenEclipsis);
    }

    private static void drawSplashScreenEclipsis() {
        setGL();
        Minecraft mc = Minecraft.getMinecraft();
        mc.updateDisplay();

        Texture logo = new Texture(new ResourceLocation("eclipsis:textures/gui/title/logo.png"));
        Texture progressBar = new Texture(new ResourceLocation("eclipsis:textures/gui/title/progress_bar.png"));
        Texture progressBarBackground = new Texture(new ResourceLocation("eclipsis:textures/gui/title/progress_bar_background.png"));
        Texture progressBarShine = new Texture(new ResourceLocation("eclipsis:textures/gui/title/progress_bar_shine.png"));

        long updateTiming = 0;
        long framecount = 0;

        float progress = 0;
        float prevProgress = 0;
        float progressAnimationTicks = 0;

        while (!done) {
            prevProgress = progress;
            progress = ClientProxy.loadingStep / 7.0F + ClientProxy.loadingPercent / 7.0F;

            if (progressAnimationTicks > 0) progressAnimationTicks -= 0.002;
            if (progress != prevProgress) progressAnimationTicks += progress - prevProgress;

            GlStateManager.clearColor(0.184F, 0.169F, 0.165F, 1.0F);
            GlStateManager.clear(GL_COLOR_BUFFER_BIT);

            // matrix setup
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, 1, -1);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            glEnable(GL_TEXTURE_2D);
            // Render splash screen
            Tessellator t = Tessellator.getInstance();
            BufferBuilder b = t.getBuffer();

            logo.bind();
            b.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            b.pos(mc.displayWidth / 2 - mc.displayHeight / 2, 0, 0)
                    .tex(logo.getU(0, 0), logo.getV(0, 0)).endVertex();
            b.pos(mc.displayWidth / 2 - mc.displayHeight / 2, mc.displayHeight, 0)
                    .tex(logo.getU(0, 0), logo.getV(0, 1)).endVertex();
            b.pos(mc.displayWidth / 2 + mc.displayHeight / 2, mc.displayHeight, 0)
                    .tex(logo.getU(0, 1), logo.getV(0, 1)).endVertex();
            b.pos(mc.displayWidth / 2 + mc.displayHeight / 2, 0, 0)
                    .tex(logo.getU(0, 1), logo.getV(0, 0)).endVertex();
            t.draw();

            float progressBarStartX = mc.displayWidth / 2.0F - mc.displayWidth * 0.3F;
            float progressBarEndX = mc.displayWidth / 2.0F + mc.displayWidth * 0.3F;
            float progressBarStartY = mc.displayHeight * 0.7F - mc.displayHeight * 0.055F;
            float progressBarEndY = mc.displayHeight * 0.7F + mc.displayHeight * 0.055F;

            float progressBarProgressX = progressBarEndX - (progressBarEndX - progressBarStartX) * (1 - (progress - progressAnimationTicks));

            progressBarBackground.bind();
            b.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            b.pos(progressBarStartX, progressBarStartY, 0)
                    .tex(progressBarBackground.getU(0, 0), progressBarBackground.getV(0, 0)).endVertex();
            b.pos(progressBarStartX, progressBarEndY, 0)
                    .tex(progressBarBackground.getU(0, 0), progressBarBackground.getV(0, 1)).endVertex();
            b.pos(progressBarEndX, progressBarEndY, 0)
                    .tex(progressBarBackground.getU(0, 1), progressBarBackground.getV(0, 1)).endVertex();
            b.pos(progressBarEndX, progressBarStartY, 0)
                    .tex(progressBarBackground.getU(0, 1), progressBarBackground.getV(0, 0)).endVertex();
            t.draw();


            progressBar.bind();
            b.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            b.pos(progressBarStartX, progressBarStartY, 0)
                    .tex(progressBar.getU(0, 0), progressBar.getV(0, 0)).endVertex();
            b.pos(progressBarStartX, progressBarEndY, 0)
                    .tex(progressBar.getU(0, 0), progressBar.getV(0, 1)).endVertex();
            b.pos(progressBarProgressX, progressBarEndY, 0)
                    .tex(progressBar.getU(0, progress - progressAnimationTicks), progressBar.getV(0, 1)).endVertex();
            b.pos(progressBarProgressX, progressBarStartY, 0)
                    .tex(progressBar.getU(0, progress - progressAnimationTicks), progressBar.getV(0, 0)).endVertex();
            t.draw();


            if ((progressBarProgressX - progressBarStartX) / (progressBarEndX - progressBarStartX) > 0.07F
                    && (progressBarProgressX - progressBarStartX) / (progressBarEndX - progressBarStartX) < 0.93F) {
                progressBarShine.bind();
                b.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                b.pos(progressBarProgressX - 20, progressBarStartY, 0)
                        .tex(progressBarShine.getU(0, 0), progressBarShine.getV(0, 0)).endVertex();
                b.pos(progressBarProgressX - 20, progressBarEndY, 0)
                        .tex(progressBarShine.getU(0, 0), progressBarShine.getV(0, 1)).endVertex();
                b.pos(progressBarProgressX + 20, progressBarEndY, 0)
                        .tex(progressBarShine.getU(0, 1), progressBarShine.getV(0, 1)).endVertex();
                b.pos(progressBarProgressX + 20, progressBarStartY, 0)
                        .tex(progressBarShine.getU(0, 1), progressBarShine.getV(0, 0)).endVertex();
                t.draw();
            }

            glDisable(GL_TEXTURE_2D);

            // We use mutex to indicate safely to the main thread that we're taking the display global lock
            // So the main thread can skip processing messages while we're updating.
            // There are system setups where this call can pause for a while, because the GL implementation
            // is trying to impose a framerate or other thing is occurring. Without the mutex, the main
            // thread would delay waiting for the same global display lock
            mutex.acquireUninterruptibly();
            long updateStart = System.nanoTime();
            mc.updateDisplay();
            mc.displayWidth = Display.getWidth();
            mc.displayHeight = Display.getHeight();
            // As soon as we're done, we release the mutex. The other thread can now ping the processmessages
            // call as often as it wants until we get get back here again
            long dur = System.nanoTime() - updateStart;
            if (framecount < TIMING_FRAME_COUNT) {
                updateTiming += dur;
            }
            mutex.release();

            if (pause) {
                clearGL();
                setGL();
            }

            framecount++;

            // Such a hack - if the time taken is greater than 10 milliseconds, we're gonna guess that we're on a
            // system where vsync is forced through the swapBuffers call - so we have to force a sleep and let the
            // loading thread have a turn - some badly designed mods access Keyboard and therefore GlobalLock.lock
            // during splash screen, and mutex against the above Display.update call as a result.
            // 4 milliseconds is a guess - but it should be enough to trigger in most circumstances. (Maybe if
            // 240FPS is possible, this won't fire?)
            if (framecount >= TIMING_FRAME_COUNT && updateTiming > TIMING_FRAME_THRESHOLD) {
                if (!isDisplayVSyncForced) {
                    isDisplayVSyncForced = true;
                    FMLLog.log.info("Using alternative sync timing : {} frames of Display.update took {} nanos", TIMING_FRAME_COUNT, updateTiming);
                }
                try {
                    Thread.sleep(16);
                } catch (InterruptedException ignored) {
                }
            } else {
                if (framecount == TIMING_FRAME_COUNT) {
                    FMLLog.log.info("Using sync timing. {} frames of Display.update took {} nanos", TIMING_FRAME_COUNT, updateTiming);
                }
                Display.sync(100);
            }
        }

        logo.delete();
        progressBar.delete();
        progressBarBackground.delete();
        progressBarShine.delete();
        clearGL();
    }

    private static void setGL() {
        lock.lock();
        try {
            Display.getDrawable().makeCurrent();
        } catch (LWJGLException e) {
            FMLLog.log.error("Error setting GL context:", e);
            throw new RuntimeException(e);
        }
        GlStateManager.clearColor(0.1843F, 0.1686F, 0.1647F, 1.0F);
        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void clearGL() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.displayWidth = Display.getWidth();
        mc.displayHeight = Display.getHeight();
        mc.resize(mc.displayWidth, mc.displayHeight);

        GlStateManager.clearColor(0.1843F, 0.1686F, 0.1647F, 1.0F);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.1f);
        try {
            Display.getDrawable().releaseContext();
        } catch (LWJGLException e) {
            FMLLog.log.error("Error releasing GL context:", e);
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @reason Removes textures deletion from method
     * @author GlassSpirit
     */
    @Overwrite(remap = false)
    public static void finish() {
        if (!enabled) return;
        try {
            checkThreadState();
            done = true;
            thread.join();
            glFlush();        // process any remaining GL calls before releaseContext (prevents missing textures on mac)
            d.releaseContext();
            Display.getDrawable().makeCurrent();
        } catch (Exception e) {
            FMLLog.log.error("Error finishing SplashProgress:", e);
            disableSplash(e);
        }
    }

}
