
import { NativeModules, NativeEventEmitter, DeviceEventEmitter } from 'react-native'

let screenShotEmitter = undefined

class RNScreenshotcatchUtil {
  static startListener(callback){
    const module = NativeModules.RNScreenshotcatch
    screenShotEmitter && screenShotEmitter.removeAllListeners('Screenshotcatch')
    screenShotEmitter = Adapter.isIOS ? new NativeEventEmitter(module) : DeviceEventEmitter
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
    if(!Adapter.isIOS){
      screenShotEmitter && screenShotEmitter.removeAllListeners('Screenshotcatch')
      const screenShotEmitter = NativeModules.RNScreenshotcatch
      return screenShotEmitter.hasNavigationBar()
    }else{
      return false
    }
  }

}

export default RNScreenshotcatchUtil
