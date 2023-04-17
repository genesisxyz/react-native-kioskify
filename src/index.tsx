import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-kiosk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const Kiosk = NativeModules.Kiosk
  ? NativeModules.Kiosk
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function init(options?: { android: { customAdminReceiver: string } }) {
  Kiosk.init(options);
}

export function enable(): Promise<boolean> {
  return Kiosk.enable();
}

export function disable(): Promise<boolean> {
  return Kiosk.disable();
}
