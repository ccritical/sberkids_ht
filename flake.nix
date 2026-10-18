{
  description = "SberKids HT Android development environment";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }:
    let
      systems = [ "x86_64-linux" "aarch64-linux" ];
      forAllSystems = nixpkgs.lib.genAttrs systems;
    in
    {
      devShells = forAllSystems (system:
        let
          pkgs = import nixpkgs {
            inherit system;
            config = {
              allowUnfree = true;
              android_sdk.accept_license = true;
            };
          };
          androidSdk = pkgs.androidenv.composeAndroidPackages {
            platformVersions = [ "35" "36" ];
            buildToolsVersions = [ "35.0.0" "36.0.0" ];
            includeEmulator = false;
            includeSystemImages = false;
            includeSources = false;
            includeNDK = false;
            useGoogleAPIs = false;
            useGoogleTVAddOns = false;
          };
        in
        {
          default = pkgs.mkShell {
            packages = [
              pkgs.jdk21
              androidSdk.androidsdk
              (pkgs.writeShellScriptBin "apk-build" ''
                exec ./gradlew --no-daemon --max-workers=2 \
                  -Dorg.gradle.jvmargs="-Xmx1536m -Dfile.encoding=UTF-8" \
                  assembleDebug
              '')
              (pkgs.writeShellScriptBin "apk-build-release" ''
                exec ./gradlew --no-daemon --max-workers=2 \
                  -Dorg.gradle.jvmargs="-Xmx1536m -Dfile.encoding=UTF-8" \
                  assembleRelease
              '')
              (pkgs.writeShellScriptBin "apk-install-waydroid" ''
                set -e
                apk="app/build/outputs/apk/debug/app-debug.apk"
                ./gradlew --no-daemon --max-workers=2 \
                  -Dorg.gradle.jvmargs="-Xmx1536m -Dfile.encoding=UTF-8" \
                  assembleDebug
                command -v waydroid >/dev/null || {
                  echo "waydroid is not installed on this host" >&2
                  exit 127
                }
                waydroid app install "$apk"
              '')
              (pkgs.writeShellScriptBin "waydroid-start" ''
                set -e
                sudo systemctl start waydroid-container
                if ! waydroid status | grep -q 'Session:[[:space:]]*RUNNING'; then
                  waydroid session start >/tmp/waydroid-session.log 2>&1 &
                  for _ in $(seq 1 30); do
                    waydroid status | grep -q 'Session:[[:space:]]*RUNNING' && break
                    sleep 1
                  done
                fi
                booted=false
                for _ in $(seq 1 45); do
                  if sudo waydroid shell getprop sys.boot_completed 2>/dev/null | grep -q '^1$'; then
                    booted=true
                    break
                  fi
                  sleep 1
                done
                if [ "$booted" != true ]; then
                  echo "Waydroid Android did not finish booting" >&2
                  cat /tmp/waydroid-session.log >&2 || true
                  exit 1
                fi
                waydroid status
                exec waydroid show-full-ui
              '')
              (pkgs.writeShellScriptBin "apk-install-adb" ''
                set -e
                apk="app/build/outputs/apk/debug/app-debug.apk"
                ./gradlew --no-daemon --max-workers=2 \
                  -Dorg.gradle.jvmargs="-Xmx1536m -Dfile.encoding=UTF-8" \
                  assembleDebug
                exec adb install -r "$apk"
              '')
              (pkgs.writeShellScriptBin "apk-clean" ''
                exec ./gradlew --no-daemon clean
              '')
            ];

            ANDROID_HOME = "${androidSdk.androidsdk}/libexec/android-sdk";
            ANDROID_SDK_ROOT = "${androidSdk.androidsdk}/libexec/android-sdk";
            JAVA_HOME = "${pkgs.jdk21}/lib/openjdk";

            shellHook = ''
              export PATH="$ANDROID_HOME/platform-tools:$PATH"
              export GRADLE_USER_HOME="''${GRADLE_USER_HOME:-$HOME/.gradle}"

              echo "Android dev shell: apk-build | apk-build-release | apk-install-waydroid | waydroid-start | apk-install-adb | apk-clean"
            '';
          };
        });
    };
}
