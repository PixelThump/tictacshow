package com.pixelthump.tictacshow.service;

import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import com.pixelthump.seshtypelib.repository.model.command.Command;

public interface CommandProcessor {
	TicTacShowState processCommand(Command command, TicTacShowState state);
}
