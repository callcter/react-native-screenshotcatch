
import { NativeModules, NativeEventEmitter, DeviceEventEmitter, Platform } from 'react-native'

let screenShotEmitter = undefined

class RNScreenshotcatchUtil {
  static startListener(callback){
    const module = NativeModules.RNScreenshotcatch
    screenShotEmitter && screenShotEmitter.removeAllListeners('Screenshotcatch')
    screenShotEmitter = Platform.OS === "ios" ? new NativeEventEmitter(module) : DeviceEventEmitter
    screenShotEmitter.addListener('Screenshotcatch', (data) => {
      if(callback){
        callback(data)
      }
    })
    module.startListener()
    return screenShotEmitter
  }

  static stopListener () {
    screenShotEmitter && screenShotEmitter.removeAllListeners('Screenshotcatch')
    const screenShotEmitter = NativeModules.RNScreenshotcatch
    return screenShotEmitter.stopListener()
  }

  static hasNavigationBar(){
    if(!Platform.OS === "ios"){
      screenShotEmitter && screenShotEmitter.removeAllListeners('Screenshotcatch')
      const screenShotEmitter = NativeModules.RNScreenshotcatch
      return screenShotEmitter.hasNavigationBar()
    }else{
      return false
    }
  }

}

export default RNScreenshotcatchUtil
