with import <nixpkgs> {};
let
  gems = bundlerEnv {
    name = "jekyll-website";
    ruby = ruby_3_1;
    gemdir = ./.;
  };
in mkShell {
  packages = [ gems gems.wrappedRuby glibcLocales ];

  shellHook = ''
    exec ${gems}/bin/jekyll serve --watch
  '';
}
