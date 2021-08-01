package com.github.pseudoresonance.resonantbot.fun;

import com.github.pseudoresonance.resonantbot.CommandManager;
import com.github.pseudoresonance.resonantbot.api.Plugin;

public class Fun extends Plugin {

	public void onEnable() {
		CommandManager.registerCommand(this, new MinesweeperCommand(), "minesweeper", "fun.minesweeperCommandDescription");
	}
	
	public void onDisable() {
	}
	
}
