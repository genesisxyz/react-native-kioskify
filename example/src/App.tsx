import * as React from 'react';

import { StyleSheet, View, Button } from 'react-native';
import * as Kiosk from 'react-native-kiosk';

Kiosk.init();

export default function App() {
  const enable = () => {
    Kiosk.enable();
  };

  const disable = () => {
    Kiosk.disable();
  };

  return (
    <View style={styles.container}>
      <Button title="Enable" onPress={enable} />
      <Button title="Disable" onPress={disable} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
