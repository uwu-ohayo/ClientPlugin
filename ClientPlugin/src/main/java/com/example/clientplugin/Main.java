package com.example.clientplugin;

import com.example.clientplugin.command.ToggleCommand;
import ovh.cubecast.cubeapi.api.AdvancedPlugin;

public final class Main extends AdvancedPlugin {

    @Override
    public void whenEnabled() {
        saveDefaultConfig();
        registerCommand(new ToggleCommand(this));
    }

}
