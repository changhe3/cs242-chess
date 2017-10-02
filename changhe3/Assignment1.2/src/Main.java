import models.Board;
import models.Player;
import util.Shorthand;

import java.awt.*;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        Board board = Board.defaultBoard(new Player.Black(), new Player.White());
        Player currPlayer = board.WHITE;
        while (true) {
            System.out.format("%s\n", board);
            System.out.format("Current Player: %s\n", currPlayer.ID);
            final Map<Point, List<Board.Operation>> allOps = board.generateMoves(currPlayer).collect(Collectors.groupingBy(op -> op.FROM));
            if (allOps.isEmpty()) {
                if (board.inCheck(currPlayer)) {
                    System.out.format("Checkmate!\n");
                } else {
                    System.out.format("Stalemate!\n");
                }
                return;
            }

            Board.Operation selected = null;
            while (selected == null) {
                System.out.println("Select your piece: ");
                // read location string
                final String line = console.nextLine();
                assert line.length() == 2;
                Point pt = Shorthand.pos(line);
                final List<Board.Operation> ops = allOps.get(pt);
                if (ops == null) {
                    System.out.println("No valid moves");
                    continue;
                }
                final ListIterator<Board.Operation> it = ops.listIterator();
                while (it.hasNext()) {
                    final int index = it.nextIndex();
                    final Board.Operation op = it.next();
                    System.out.format("%s) %s \n", index + 1, op);
                }

                System.out.println("Select your move: ");
                int option = -1;
                while (option == -1) {
                    final String optionStr = console.nextLine();
                    if (optionStr.equals("b")) {
                        option = 0;
                    } else {
                        try {
                            option = Integer.valueOf(optionStr);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Input. Reselect your move: ");
                        }
                    }
                }
                if (option == 0) {
                    continue;
                }
                selected = ops.get(option - 1);
                board.execute(selected, currPlayer);
                currPlayer = board.theOther(currPlayer);
            }
        }
    }
}
