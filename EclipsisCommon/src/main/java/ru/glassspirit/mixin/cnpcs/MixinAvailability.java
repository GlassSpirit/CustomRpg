package ru.glassspirit.mixin.cnpcs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.NBTTags;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.data.Availability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.glassspirit.cnpcs.data.IMixinAvailability;
import ru.glassspirit.cnpcs.data.IScriptContainerExtended;
import ru.glassspirit.cnpcs.event.AvailabilityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Mixin(Availability.class)
public abstract class MixinAvailability implements IMixinAvailability {

    private List<ScriptContainer> scripts;
    private String scriptLanguage;
    private boolean enabled;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        scripts = new ArrayList<>();
        scriptLanguage = "ECMAScript";
        enabled = false;
    }

    @Inject(method = "isAvailable(Lnet/minecraft/entity/player/EntityPlayer;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void isAvailableInject(EntityPlayer player, CallbackInfoReturnable<Boolean> ci) {
        if (enabled) {
            AvailabilityEvent event = new AvailabilityEvent(player);
            WrapperNpcAPI.EVENT_BUS.post(event);
            for (ScriptContainer s : this.getScripts()) {
                Object checkObject = ((IScriptContainerExtended) s).invoke("check", event);
                if (checkObject instanceof Boolean && !((Boolean) checkObject)) {
                    ci.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"), remap = false)
    private void readFromNbtInject(NBTTagCompound compound, CallbackInfo info) {
        readScriptFromNbt(compound);
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    private void writeToNbtInject(NBTTagCompound compound, CallbackInfoReturnable info) {
        writeScriptToNbt(compound);
    }

    @Override
    public void readScriptFromNbt(NBTTagCompound compound) {
        this.scripts = NBTTags.GetScript(compound.getTagList("Scripts", 10), this);
        this.scriptLanguage = compound.getString("ScriptLanguage");
        this.enabled = compound.getBoolean("ScriptEnabled");
    }

    @Override
    public NBTTagCompound writeScriptToNbt(NBTTagCompound compound) {
        compound.setTag("Scripts", NBTTags.NBTScript(this.scripts));
        compound.setString("ScriptLanguage", this.scriptLanguage);
        compound.setBoolean("ScriptEnabled", this.enabled);
        return compound;
    }

    @Override
    public void runScript(EnumScriptType enumScriptType, Event event) {
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    @Override
    public String getLanguage() {
        return scriptLanguage;
    }

    @Override
    public void setLanguage(String s) {
        this.scriptLanguage = s;
    }

    @Override
    public List<ScriptContainer> getScripts() {
        return this.scripts;
    }

    @Override
    public String noticeString() {
        return "some Availability";
    }

    @Override
    public Map<Long, String> getConsoleText() {
        Map<Long, String> map = new TreeMap<>();
        int tab = 0;

        for (ScriptContainer script : this.getScripts()) {
            ++tab;

            for (Map.Entry<Long, String> longStringEntry : script.console.entrySet()) {
                map.put(longStringEntry.getKey(), " tab " + tab + ":\n" + longStringEntry.getValue());
            }
        }

        return map;
    }

    @Override
    public void clearConsole() {
        for (ScriptContainer script : this.getScripts()) {
            script.console.clear();
        }
    }
}
