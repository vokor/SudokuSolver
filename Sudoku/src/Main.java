import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    static int getNum(int a, int b, int c) {
        return a * 81 + b * 9 + c;
    }

    static void makeFile(int[][] a, int countLines) throws Exception {
        FileWriter writer = new FileWriter("bool.txt");
        writer.write("p cnf 729 " + (23409 + countLines) + "\r\n");
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                for (int k = 1; k < 10; k++)
                    for (int t = 1; t < 10; t++)
                        if (t != k)
                            writer.write("-" + getNum(i, j, k) + " -" + getNum(i, j, t) + " 0\r\n");

        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                for (int k = 1; k < 10; k++)
                    writer.write(getNum(i, j, k) + " ");
                writer.write("0\r\n");
            }

        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                for (int k = 0; k < 9; k++)
                    for (int t = 1; t < 10; t++)
                        if (j != k)
                            writer.write("-" + getNum(i, j, t) + " -" + getNum(i, k, t) + " 0\r\n");

        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                for (int k = 0; k < 9; k++)
                    for (int t = 1; t < 10; t++)
                        if (j != k)
                            writer.write("-" + getNum(j, i, t) + " -" + getNum(k, i, t) + " 0\r\n");

        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        for (int k = 0; k < 3; k++)
                            for (int t = 0; t < 3; t++)
                                for (int num = 1; num < 10; num++) {
                                    int x1, y1, x2, y2;
                                    x1 = r * 3 + i; y1 = c * 3 + j;
                                    x2 = r * 3 + k; y2 = c * 3 + t;
                                    if (!(x1 == x2 && y1 == y2))
                                        writer.write("-" + getNum(x1, y1, num) + " -" + getNum(x2, y2, num) + " 0\r\n");
                                }

       for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (a[i][j] != -1)
                    writer.write(getNum(i, j, a[i][j]) + " 0\r\n");
        writer.close();
    }

    static int[][] decode(int[] a) {
        int b[][] = new int[9][9];
        for (int i = 0; i < a.length; i++)
            if (a[i] > 0) {
                b[i / 81][(i % 81) / 9] = (i % 9) + 1;
            }
        return b;
    }

    public static void main(String[] args) throws Exception {
        File file = new File("in.txt");

        Scanner scan = new Scanner(file);

        int[][] a;
        a = new int[9][9];
        int it = 0, tt = 0;
        while (scan.hasNextLine() && it < 9) {
            String line = scan.nextLine();
            for (int i = 0; i < 9; i++)
                if (line.charAt(i) == '_')
                    a[it][i] = -1;
                else {
                    a[it][i] = line.charAt(i) - '0';
                    tt++;
                }
            it++;
        }
        System.out.println();
        scan.close();

        makeFile(a, tt);

        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600); // 1 hour timeout
        DimacsReader reader = new DimacsReader(solver);

        try {
            IProblem problem = reader.parseInstance("bool.txt");
            if (problem.isSatisfiable()) {
                System.out.println("Satisfiable!");
                FileWriter writer = new FileWriter("out.txt");
                int b[][] = decode(problem.model());
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        System.out.print(b[i][j]);
                        //writer.write(b[i][j]);
                    }
                    System.out.println();
                    //writer.write("\r\n");
                }
                writer.close();
            } else {
                FileWriter writer = new FileWriter("out.txt");
                writer.write("Unsatisfiable!");
                writer.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        } catch (ParseFormatException e) {
            System.out.println("format exception");
        } catch (IOException e) {
            System.out.println("io exception");
        } catch (ContradictionException e) {
            System.out.println("Unsatisfiable (trivial)!");
        } catch (TimeoutException e) {
            System.out.println("Timeout , sorry!");
        }

    }
}
