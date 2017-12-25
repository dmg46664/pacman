//
// Pacman - the Scaled package manager
// https://github.com/scaled/pacman/blob/master/LICENSE

package scaled.pacman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Primitive helpers for running subprocesses.
 */
public class Exec {

  public static class Handle {
    public int waitFor () throws IOException {
      _pb.inheritIO();
      Process p = _pb.start();
      try { return p.waitFor(); }
      catch (InterruptedException e) { throw new IOException("Process.waitFor() interrupted"); }
    }

    public void expect (int exitCode, String onError) throws IOException {
      if (waitFor() != exitCode) throw new IOException(onError);
    }

    public List<String> output () throws IOException {
      Process p = _pb.start();
      BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
      List<String> output = new ArrayList<>();
      String line;
      while ((line = in.readLine()) != null) output.add(line);
      return output;
    }

    public List<String> error () throws IOException {
      Process p = _pb.start();
      BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      List<String> output = new ArrayList<>();
      String line;
      while ((line = in.readLine()) != null) output.add(line);
      return output;
    }

    private Handle (ProcessBuilder pb) {
      _pb = pb;
    }
    private final ProcessBuilder _pb;
  }

  public static Handle exec (Path cwd, List<String> cmd) throws IOException {
    return exec(cwd, cmd.toArray(new String[cmd.size()]));
  }

  public static Handle exec (Path cwd, String... cmd) throws IOException {
    // resolve the real CWD to avoid problems with symlinks and Windows
    Path realCwd = cwd.toRealPath();
    if (Props.debug) {
      System.out.println("Exec in " + realCwd);
      System.out.println("  " + cmd[0]);
      boolean cont = false;
      for (int ii = 1; ii < cmd.length; ii++) {
        String arg = cmd[ii];
        if (cont) { System.out.println(" " + arg); cont = false; }
        else if (!arg.startsWith("-")) System.out.println("     " + arg);
        else { System.out.print("     " + arg); cont = true; }
      }
      if (cont) System.out.println();
    }

    ProcessBuilder proc = new ProcessBuilder(cmd);
    proc.directory(realCwd.toFile());
    return new Handle(proc);
  }
}
