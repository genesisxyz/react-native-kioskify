# react-native-kiosk

Kiosk mode

## Installation

```sh
npm install react-native-kiosk
```

or

```sh
yarn add react-native-kiosk
```

## Usage

### Android

To enable admin you need to run:

```bash
adb shell dpm set-device-owner com.kioskexample/com.kiosk.MyDeviceAdminReceiver
```

To remove admin instead do:

```bash
adb shell dpm remove-active-admin com.kioskexample/com.kiosk.MyDeviceAdminReceiver
```

To kill the app you can use:

```bash
adb shell ps | grep packageinstaller | awk '{print $2}' | xargs adb shell kill
```

Change `com.kioskexample` to your bundle id

If you want the app to be able to run automatically as default you need to add to your `AndroidManifest.xml` inside `<intent-filter>`:

```xml
<action android:name="android.intent.action.MAIN" />
<category android:name="android.intent.category.LAUNCHER" />
// add these next 2 lines
<category android:name="android.intent.category.DEFAULT" />
<category android:name="android.intent.category.HOME" />
```

Also, on `<Application>` add:

```xml
android:testOnly="true"
```

Remember to remove this line for release

```js
import Kiosk from 'react-native-kiosk';

// ...

await Kiosk.enable();
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
