package ru.glassspirit.mixin.cnpcs;

import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.CustomNpcsConfig;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.glassspirit.cnpcs.data.IScriptContainerExtended;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashSet;

@Mixin(ScriptContainer.class)
public abstract class MixinScriptContainer implements IScriptContainerExtended {

    @Shadow(remap = false)
    public static ScriptContainer Current;
    @Shadow(remap = false)
    private static String CurrentType;
    @Shadow(remap = false)
    public boolean errored;
    @Shadow(remap = false)
    private HashSet<String> unknownFunctions;
    @Shadow(remap = false)
    public long lastCreated;
    @Shadow(remap = false)
    private ScriptEngine engine;
    @Shadow(remap = false)
    private IScriptHandler handler;
    @Shadow(remap = false)
    private boolean init;
    @Shadow(remap = false)
    private static Method luaCoerce;
    @Shadow(remap = false)
    private static Method luaCall;

    @Shadow(remap = false)
    public abstract void setEngine(String scriptLanguage);

    @Shadow(remap = false)
    public abstract boolean hasCode();

    @Shadow(remap = false)
    protected abstract String getFullCode();

    @Shadow(remap = false)
    public abstract void appandConsole(String message);

    @Shadow(remap = false)
    @Final
    private static String lock;

    @Override
    public Object invoke(String function, Event event) {
        if (!this.errored && this.hasCode() && !this.unknownFunctions.contains(function) && CustomNpcsConfig.EnableScripting) {
            this.setEngine(this.handler.getLanguage());
            if (this.engine != null) {
                if (ScriptController.Instance.lastLoaded > this.lastCreated) {
                    this.lastCreated = ScriptController.Instance.lastLoaded;
                    this.init = false;
                }

                synchronized (lock) {
                    //Current = this;
                    CurrentType = function;
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    this.engine.getContext().setWriter(pw);
                    this.engine.getContext().setErrorWriter(pw);

                    try {
                        if (!this.init) {
                            this.engine.eval(this.getFullCode());
                            this.init = true;
                        }

                        if (this.engine.getFactory().getLanguageName().equals("lua")) {
                            Object ob = this.engine.get(function);
                            if (ob != null) {
                                if (luaCoerce == null) {
                                    luaCoerce = Class.forName("org.luaj.vm2.lib.jse.CoerceJavaToLua").getMethod("coerce", Object.class);
                                    luaCall = ob.getClass().getMethod("call", Class.forName("org.luaj.vm2.LuaValue"));
                                }

                                return luaCall.invoke(ob, luaCoerce.invoke(null, event));
                            } else {
                                this.unknownFunctions.add(function);
                            }
                        } else {
                            return ((Invocable) this.engine).invokeFunction(function, event);
                        }
                    } catch (NoSuchMethodException var13) {
                        this.unknownFunctions.add(function);
                    } catch (Throwable var14) {
                        this.errored = true;
                        var14.printStackTrace(pw);
                        NoppesUtilServer.NotifyOPs(this.handler.noticeString() + " script errored");
                    } finally {
                        this.appandConsole(sw.getBuffer().toString().trim());
                        pw.close();
                        Current = null;
                    }

                }
            }
        }
        return null;
    }
}
