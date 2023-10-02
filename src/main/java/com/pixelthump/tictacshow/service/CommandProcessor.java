package com.pixelthump.tictacshow.service;

import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import com.pixelthump.seshtypelib.repository.model.command.Command;

public interface CommandProcessor {
	void processCommand(Command command, TicTacShowState state);
}
