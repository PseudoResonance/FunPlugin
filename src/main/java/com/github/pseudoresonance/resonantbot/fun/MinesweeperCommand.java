package com.github.pseudoresonance.resonantbot.fun;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;

import com.github.pseudoresonance.resonantbot.api.Command;
import com.github.pseudoresonance.resonantbot.language.Language;
import com.github.pseudoresonance.resonantbot.language.LanguageManager;
import com.github.pseudoresonance.resonantbot.permissions.PermissionGroup;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MinesweeperCommand extends Command {
	
	private static final Random rand = new Random();
	
	public MinesweeperCommand() {
		super();
	}

	@Override
	public void onCommand(MessageReceivedEvent e, String command, HashSet<PermissionGroup> userPermissions, String[] args) {
		Language lang = LanguageManager.getLanguage(e);
		EmbedBuilder embed = new EmbedBuilder();
		DIFFICULTY_LEVEL difficulty = DIFFICULTY_LEVEL.LEVEL_2;
		int size = 7;
		int numBombs = 17;
		if (args.length == 1) {
			try {
				difficulty = DIFFICULTY_LEVEL.getDifficultyLevel(Integer.valueOf(args[0]));
			} catch (NumberFormatException ex) {}
		} else if (args.length >= 2) {
			try {
				size = Integer.valueOf(args[0]);
				numBombs = Integer.valueOf(args[1]);
				difficulty = null;
				if (size > 10)
					size = 10;
				else if (size < 2)
					size = 2;
				if (numBombs >= size * size)
					numBombs = size * size - 1;
				if (numBombs < 1)
					numBombs = 1;
			} catch (NumberFormatException ex) {}
		}
		embed.setColor(new Color(190, 30, 30));
		embed.setTitle(lang.getMessage("fun.minesweeper"));
		if (difficulty != null) {
			embed.setDescription(lang.getMessage("fun.difficultyLevel", difficulty.DIFFICULTY));
			embed.addField(difficulty.SIZE + "×" + difficulty.SIZE, generateGame(difficulty), true);
		} else {
			embed.setDescription(lang.getMessage("fun.numBombs", numBombs));
			embed.addField(size + "×" + size, generateGame(size, numBombs), true);
		}
		embed.setTimestamp(LocalDateTime.now());
		e.getTextChannel().sendMessageEmbeds(embed.build()).queue();
	}
	
	private String generateGame(DIFFICULTY_LEVEL difficulty) {
		return generateGame(difficulty.SIZE, difficulty.BOMBS);
	}
	
	private String generateGame(int size, int bombs) {
		int[][] game = new int[size][size];
		game = fillBombs(game, bombs);
		game = fillCounts(game);
		game = findStartingSquare(game, 0);
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < game.length; x++) {
			for (int y = 0; y < game[x].length; y++) {
				sb.append(getEmoji(game[x][y]));
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private int[][] fillBombs(int[][] game, int bombs) {
		int x, y;
		for (int i = 0; i < bombs; i++) {
			x = rand.nextInt(game.length);
			y = rand.nextInt(game[x].length);
			if (game[x][y] == 0)
				game[x][y] = 9;
			else
				i--;
		}
		return game;
	}
	
	private int[][] fillCounts(int[][] game) {
		for (int x = 0; x < game.length; x++) {
			for (int y = 0; y < game[x].length; y++) {
				if (game[x][y] != 9) {
					game[x][y] = getBombsAround(game, x, y);
				}
			}
		}
		return game;
	}
	
	private int getBombsAround(int[][] game, int x, int y) {
		int count = 0;
		for (int i = Math.max(x - 1, 0); i < Math.min(x + 2, game.length); i++) {
			for (int j = Math.max(y - 1, 0); j < Math.min(y + 2, game[i].length); j++) {
				if (game[i][j] == 9) {
					count++;
				}
			}
		}
		return count;
	}
	
	private int[][] findStartingSquare(int[][] game, int maxBombs) {
		int initX = rand.nextInt(game.length);
		int initY = rand.nextInt(game[initX].length);
		int x = initX;
		int y = initY;
		while (true) {
			if (game[x][y] == maxBombs) {
				if (maxBombs == 0)
					game[x][y] = -9;
				else
					game[x][y] = -maxBombs;
				return game;
			}
			if (++y >= game[x].length) {
				y = 0;
				if (++x >= game.length) {
					x = 0;
				}
			}
			if (x == initX && y == initY)
				break;
		}
		if (maxBombs < 8)
			return findStartingSquare(game, ++maxBombs);
		return game;
	}
	
	private String getEmoji(int value) {
		switch (value) {
		case -1:
			return "1️⃣";
		case -2:
			return "2️⃣";
		case -3:
			return "3️⃣";
		case -4:
			return "4️⃣";
		case -5:
			return "5️⃣";
		case -6:
			return "6️⃣";
		case -7:
			return "7️⃣";
		case -8:
			return "8️⃣";
		case -9:
			return "0️⃣";
		case 0:
			return "||0️⃣||";
		case 1:
			return "||1️⃣||";
		case 2:
			return "||2️⃣||";
		case 3:
			return "||3️⃣||";
		case 4:
			return "||4️⃣||";
		case 5:
			return "||5️⃣||";
		case 6:
			return "||6️⃣||";
		case 7:
			return "||7️⃣||";
		case 8:
			return "||8️⃣||";
		case 9:
			return "||💣||";
		}
		return "" + value;
	}

	enum DIFFICULTY_LEVEL {
		
		LEVEL_1(1, 5, 6),
		LEVEL_2(2, 7, 15),
		LEVEL_3(3, 9, 36);
		
		public final int DIFFICULTY;
		public final int SIZE;
		public final int BOMBS;
		
		private DIFFICULTY_LEVEL(int difficulty, int size, int bombs) {
			this.DIFFICULTY = difficulty;
			this.SIZE = size;
			this.BOMBS = bombs;
		}
		
		public static DIFFICULTY_LEVEL getDifficultyLevel(int difficulty) {
			for (DIFFICULTY_LEVEL level : values()) {
				if (level.DIFFICULTY == difficulty) {
					return level;
				}
			}
			return LEVEL_2;
		}
	}

}
