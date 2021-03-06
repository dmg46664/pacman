//
// Pacman - the Scaled package manager
// https://github.com/scaled/pacman/blob/master/LICENSE

package scaled.pacman;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Wraps our supported DVCSes in a Java API. Warning: ultra primitive!
 */
public abstract class VCSDriver {

  /** Returns true if {@code dir} already contains a checkout of {@code url}. */
  public abstract boolean exists (URI url, Path dir) throws IOException;

  /** Checks out {@code url} into {@code path}. */
  public abstract void checkout (URI url, Path into) throws IOException;

  /** Fetches (but does not apply) any available updates for the checkout in {@code path}. */
  public abstract void fetch (Path path) throws IOException;

  /** Applies any fetched updates to the checkout in {@code path}. */
  public abstract void update (Path path) throws IOException;

  public static VCSDriver get (Source.VCS vcs) {
    switch (vcs) {
      case GIT: return new GitDriver();
      case HG:  return new HgDriver();
      case SVN: return new SubversionDriver();
      default: throw new IllegalArgumentException("Unknown VCS " + vcs);
    }
  }

  protected static class GitDriver extends VCSDriver {
    public boolean exists (URI url, Path dir) throws IOException {
      return url.toString().equals(readOrigin(dir));
    }

    public void checkout (URI url, Path into) throws IOException {
      Exec.exec(into.getParent(), "git", "clone", "-q", url.toString(),
                into.getFileName().toString()).expect(0, "git clone failed");
    }

    public void fetch (Path path) throws IOException {
      Exec.exec(path, "git", "fetch").expect(0, "git fetch failed");
    }

    public void update (Path path) throws IOException {
      // TODO: we probably want some fancy args here, like -ff or something
      Exec.exec(path, "git", "pull").expect(0, "git pull failed");
    }

    private String readOrigin (Path root) throws IOException {
      // if there's no .git directory here, don't run git because it will search up the directory
      // heirarchy for a .git dir which is not what we want
      if (!Files.exists(root.resolve(".git"))) return "";
      // if there is a .git directory, then we can run 'git remote -v' to see what we have
      for (String line : Exec.exec(root, "git", "remote", "-v").output()) {
        String[] bits = line.split("\\s");
        if (bits[0].equals("origin")) return bits[1];
      }
      return "";
    }
  }

  protected static class HgDriver extends VCSDriver {
    public boolean exists (URI url, Path dir) throws IOException {
      return false; // TODO
    }

    public void checkout (URI url, Path into) throws IOException {
      throw new IOException("TODO");
    }

    public void fetch (Path path) throws IOException {
      throw new IOException("TODO");
    }

    public void update (Path path) throws IOException {
      throw new IOException("TODO");
    }
  }

  protected static class SubversionDriver extends VCSDriver {
    public boolean exists (URI url, Path dir) throws IOException {
      return false; // TODO
    }

    public void checkout (URI url, Path into) throws IOException {
      throw new IOException("TODO");
    }

    public void fetch (Path path) throws IOException {
      throw new IOException("TODO");
    }

    public void update (Path path) throws IOException {
      throw new IOException("TODO");
    }
  }
}
