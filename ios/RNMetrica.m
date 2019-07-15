#import "RNMetrica.h"

#import <YandexMobileMetrica/YandexMobileMetrica.h>
#import <CoreLocation/CoreLocation.h>

#import <React/RCTUtils.h>

@implementation RNMetrica

static bool gYMMIsAppMetricaActivated = false;

RCT_EXPORT_MODULE();

- (dispatch_queue_t)methodQueue {
    return dispatch_queue_create("com.facebook.React.AsyncLocalStorageQueue", DISPATCH_QUEUE_SERIAL);
}

RCT_EXPORT_METHOD(activate:(NSDictionary *)configurationDictionary)
{
    [[self class] activateWithConfigurationDictionary:configurationDictionary];
}

RCT_REMAP_METHOD(isActivated,
                 isActivatedWithResolver:(RCTPromiseResolveBlock)resolve
                 rejecter: (RCTPromiseRejectBlock)reject)
{
    return resolve([NSNumber numberWithBool:gYMMIsAppMetricaActivated]);
}

RCT_EXPORT_METHOD(handleOpenURL:(NSString *)urlString)
{
    NSURL *url = [NSURL URLWithString:urlString];
    if (url != nil) {
        [YMMYandexMetrica handleOpenURL:url];
    }
}


RCT_EXPORT_METHOD(reportReferralUrl:(NSString *)urlString)
{
    NSURL *url = [NSURL URLWithString:urlString];
    if (url != nil) {
        [YMMYandexMetrica reportReferralUrl:url];
    }
}

RCT_EXPORT_METHOD(reportEvent:(NSString *)eventName value:(NSDictionary *)eventParams callback:(RCTResponseSenderBlock)callback)
{
    [YMMYandexMetrica reportEvent:eventName
                      parameters:eventParams
                        onFailure:^(NSError *error) {
                            NSDictionary *jsError = RCTMakeError(@"Failed to report event", error, nil);
                            callback(@[@[jsError]]);
                        }];
}

RCT_EXPORT_METHOD(reportError:(NSString *)errorName value:(NSString *)errorReason callback:(RCTResponseSenderBlock)callback)
{
    NSException *exception = [NSException exceptionWithName:errorName reason:errorReason userInfo:nil];
    [YMMYandexMetrica reportError:errorName
                      exception:exception
                        onFailure:^(NSError *error) {
                            NSDictionary *jsError = RCTMakeError(@"Failed to report error", error, nil);
                            callback(@[@[jsError]]);
                        }];
}

RCT_EXPORT_METHOD(setLocation:(NSDictionary *)locationDictionary)
{
    CLLocation *location = [[self class] locationForDictionary:locationDictionary];
    [YMMYandexMetrica setLocation:location];
}

RCT_EXPORT_METHOD(setLocationTracking:(NSNumber *)enabledValue)
{
    if (enabledValue != nil) {
        [YMMYandexMetrica setLocationTracking:enabledValue.boolValue];
    }
}

RCT_EXPORT_METHOD(setProfileID:(NSString *)id)
{
    [YMMYandexMetrica setUserProfileID:id];
}

#pragma mark - Utils

+ (YMMYandexMetricaConfiguration *)configurationForDictionary:(NSDictionary *)configurationDictionary
{
    NSString *apiKey = configurationDictionary[@"apiKey"];
    YMMYandexMetricaConfiguration *configuration = [[YMMYandexMetricaConfiguration alloc] initWithApiKey:apiKey];

    NSNumber *handleFirstActivationAsUpdate = configurationDictionary[@"handleFirstActivationAsUpdate"];
    NSNumber *locationTracking = configurationDictionary[@"locationTracking"];
    NSNumber *sessionTimeout = configurationDictionary[@"sessionTimeout"];
    NSNumber *crashReporting = configurationDictionary[@"crashReporting"];
    NSString *appVersion = configurationDictionary[@"appVersion"];
    NSNumber *logs = configurationDictionary[@"logs"];
    NSDictionary *customLocationDictionary = configurationDictionary[@"location"];
    NSDictionary *preloadInfoDictionary = configurationDictionary[@"preloadInfo"];

    if (handleFirstActivationAsUpdate != nil) {
        configuration.handleFirstActivationAsUpdate = [handleFirstActivationAsUpdate boolValue];
    }
    if (locationTracking != nil) {
        configuration.locationTracking = [locationTracking boolValue];
    }
    if (sessionTimeout != nil) {
        configuration.sessionTimeout = [sessionTimeout unsignedIntegerValue];
    }
    if (crashReporting != nil) {
        configuration.crashReporting = [crashReporting boolValue];
    }
    if (appVersion != nil) {
        configuration.appVersion = appVersion;
    }
    if (logs != nil) {
        configuration.logs = [logs boolValue];
    }
    if (customLocationDictionary != nil) {
        configuration.location = [self locationForDictionary:customLocationDictionary];;
    }
    if (preloadInfoDictionary != nil) {
        NSString *trackingID = preloadInfoDictionary[@"trackingId"];
        YMMYandexMetricaPreloadInfo *preloadInfo =
        [[YMMYandexMetricaPreloadInfo alloc] initWithTrackingIdentifier:trackingID];
        NSDictionary *additionalInfo = preloadInfoDictionary[@"additionalInfo"];
        for (NSString *key in additionalInfo) {
            [preloadInfo setAdditionalInfo:additionalInfo[key] forKey:key];
        }
        configuration.preloadInfo = preloadInfo;
    }

    return configuration;
}

+ (CLLocation *)locationForDictionary:(NSDictionary *)locationDictionary
{
    if (locationDictionary == nil) {
        return nil;
    }

    NSNumber *latitude = locationDictionary[@"latitude"];
    NSNumber *longitude = locationDictionary[@"longitude"];
    NSNumber *altitude = locationDictionary[@"altitude"];
    NSNumber *horizontalAccuracy = locationDictionary[@"accuracy"];
    NSNumber *verticalAccuracy = locationDictionary[@"verticalAccuracy"];
    NSNumber *course = locationDictionary[@"course"];
    NSNumber *speed = locationDictionary[@"speed"];
    NSNumber *timestamp = locationDictionary[@"timestamp"];

    NSDate *locationDate = timestamp != nil ? [NSDate dateWithTimeIntervalSince1970:timestamp.doubleValue] : [NSDate date];
    CLLocationCoordinate2D coordinate = CLLocationCoordinate2DMake(latitude.doubleValue, longitude.doubleValue);
    CLLocation *location = [[CLLocation alloc] initWithCoordinate:coordinate
                                                         altitude:altitude.doubleValue
                                               horizontalAccuracy:horizontalAccuracy.doubleValue
                                                 verticalAccuracy:verticalAccuracy.doubleValue
                                                           course:course.doubleValue
                                                            speed:speed.doubleValue
                                                        timestamp:locationDate];
    return location;
}

+ (void)activateWithConfigurationDictionary:(NSDictionary *)configuration
{
    YMMYandexMetricaConfiguration *config = [[self class] configurationForDictionary:configuration];
    [YMMYandexMetrica activateWithConfiguration:config];
    gYMMIsAppMetricaActivated = true;
}

@end
