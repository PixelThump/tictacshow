package com.pixelthump.tictacshow.service;

import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pixelthump.tictacshow.repository.model.Team;
import com.pixelthump.tictacshow.repository.model.TicTacShowPlayer;
import com.pixelthump.tictacshow.repository.model.TicTacShowStage;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;

import com.pixelthump.seshtypelib.repository.CommandRespository;
import com.pixelthump.seshtypelib.repository.model.command.Command;
import com.pixelthump.seshtypelib.service.GameLogicService;
import com.pixelthump.seshtypelib.service.model.State;
import com.pixelthump.seshtypelib.service.model.player.Player;
import com.pixelthump.tictacshow.repository.TicTacShowStateRepository;
import com.pixelthump.tictacshow.repository.model.Team;
import com.pixelthump.tictacshow.repository.model.TicTacShowPlayer;
import com.pixelthump.tictacshow.repository.model.TicTacShowStage;
import com.pixelthump.tictacshow.repository.model.TicTacShowState;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainCommandProcessor implements CommandProcessor{

	public void processCommand(Command command, TicTacShowState state) {

	}
}
