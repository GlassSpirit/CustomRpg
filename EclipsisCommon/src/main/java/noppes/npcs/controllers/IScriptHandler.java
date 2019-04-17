package noppes.npcs.controllers;

import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.constants.EnumScriptType;

import java.util.List;
import java.util.Map;

public interface IScriptHandler {

    void runScript(EnumScriptType type, Event event);

    boolean isClient();

    boolean getEnabled();

    void setEnabled(boolean bo);

    String getLanguage();

    void setLanguage(String lang);

    List<ScriptContainer> getScripts();

    String noticeString();

    Map<Long, String> getConsoleText();

    void clearConsole();
}
