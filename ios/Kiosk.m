#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(Kiosk, NSObject)

RCT_EXTERN_METHOD(init:(NSDictionary *)options)

RCT_EXTERN_METHOD(enable:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(disable:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
