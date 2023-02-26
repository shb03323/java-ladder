package ladder.controller;

import ladder.common.CustomException;
import ladder.domain.ladder.Block;
import ladder.domain.ladder.Ladder;
import ladder.domain.ladder.Line;
import ladder.domain.ladder.generator.BlockGenerator;
import ladder.domain.ladder.generator.RandomBlockGenerator;
import ladder.domain.player.PlayerName;
import ladder.domain.player.Players;
import ladder.domain.result.Result;
import ladder.domain.result.Results;
import ladder.domain.LadderGame;
import ladder.view.InputView;
import ladder.view.OutputView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LadderGameController {

    private static final String SEARCH_ALL_KEYWORD = "all";

    public void run() {
        Players players = initPlayers();
        int playerNumber = players.size();
        Results results = initPrizes(playerNumber);
        Ladder ladder = initLadder(playerNumber);

        showDrawnResult(players, results, ladder);
        showAnalyzedResult(players, results, ladder);
        InputView.terminate();
    }

    private Players initPlayers() {
        try {
            List<String> playerNames = InputView.inputPlayer();
            return new Players(playerNames);
        } catch (CustomException e) {
            OutputView.printErrorMessage(e);
            return initPlayers();
        }
    }

    private Results initPrizes(final int playerNumber) {
        try {
            List<String> prizeNames = InputView.inputPrize();
            return new Results(playerNumber, prizeNames);
        } catch (CustomException e) {
            OutputView.printErrorMessage(e);
            return initPrizes(playerNumber);
        }
    }

    private Ladder initLadder(final int playerNumber) {
        try {
            final int height = InputView.inputLadderHeight();
            BlockGenerator blockGenerator = new RandomBlockGenerator();
            return new Ladder(blockGenerator, playerNumber, height);
        } catch (CustomException e) {
            OutputView.printErrorMessage(e);
            return initLadder(playerNumber);
        }
    }

    private void showDrawnResult(final Players players, final Results results, final Ladder ladder) {
        OutputView.printGameResultHeader();
        printPlayersName(players);
        OutputView.printLadder(toLines(ladder));
        printPrizesName(results);
    }

    private void printPlayersName(final Players players) {
        List<String> playersName = toPlayersName(players);
        OutputView.printPlayersName(playersName);
    }

    private List<String> toPlayersName(final Players players) {
        return players.getPlayers().stream()
                .map(PlayerName::getName)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<List<Boolean>> toLines(Ladder ladder) {
        return ladder.getLines().stream()
                .map(this::toBlocks)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<Boolean> toBlocks(Line line) {
        return line.getBlocks().stream()
                .map(Block::isExistBlock)
                .collect(Collectors.toUnmodifiableList());
    }

    private void printPrizesName(Results results) {
        List<String> prizesName = toPrizesName(results);
        OutputView.printPrizesName(prizesName);
    }

    private List<String> toPrizesName(Results results) {
        return results.getPrizes().stream()
                .map(Result::getName)
                .collect(Collectors.toUnmodifiableList());
    }

    private void showAnalyzedResult(Players players, Results results, Ladder ladder) {
        LadderGame ladderGame = new LadderGame(players, results, ladder);
        searchResult(ladderGame, players);
    }

    private void searchResult(LadderGame ladderGame, Players players) {
        try {
            chooseSearchOption(ladderGame, players);
        } catch (CustomException e) {
            OutputView.printErrorMessage(e);
            searchResult(ladderGame, players);
        }
    }

    private void chooseSearchOption(LadderGame ladderGame, Players players) {
        String input = InputView.inputPlayerResult();
        while (!input.equals(SEARCH_ALL_KEYWORD)) {
            showSinglePlayerResult(ladderGame, players, input);
            input = InputView.inputPlayerResult();
        }
        showAllPlayersResult(ladderGame);
    }

    private void showAllPlayersResult(LadderGame ladderGame) {
        OutputView.printPlayerResultHeaderMessage();
        Map<String, String> resultForView = new LinkedHashMap<>();
        Map<PlayerName, Result> results = ladderGame.getAllResult();
        for (PlayerName playerName : results.keySet()) {
            String prizeName = results.get(playerName).getName();
            resultForView.put(playerName.getName(), prizeName);
        }
        OutputView.printAllResults(resultForView);
    }

    private void showSinglePlayerResult(LadderGame ladderGame, Players players, String name) {
        OutputView.printPlayerResultHeaderMessage();
        PlayerName playerName = players.findByName(name);
        Result result = ladderGame.getSinglePlayerResult(playerName);
        OutputView.printSingleResult(result.getName());
    }
}
