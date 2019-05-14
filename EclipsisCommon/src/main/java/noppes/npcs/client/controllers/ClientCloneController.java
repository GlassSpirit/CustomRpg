package noppes.npcs.client.controllers;

import noppes.npcs.common.CustomNpcs;
import noppes.npcs.controllers.ServerCloneController;

import java.io.File;

public class ClientCloneController extends ServerCloneController {
    public static ClientCloneController Instance = new ClientCloneController();

    @Override
    public File getDir() {
        File dir = new File(CustomNpcs.INSTANCE.getDir(), "clones");
        if (!dir.exists())
            dir.mkdir();
        return dir;
    }
}
