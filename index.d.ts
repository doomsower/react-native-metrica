declare module 'react-native-metrica' {
  export interface MetricaPreloadInfo {
    trackingId?: string;
    additionalInfo?: Record<string, any>;
  }

  export interface MetricaLocation {
    latitude?: number;
    longitude?: number;
    altitude?: number;
    accuracy?: number;
    verticalAccuracy?: number;
    course?: number;
    speed?: number;
    timestamp?: number;
  }

  export interface MetricaConfig {
    apiKey: string;
    handleFirstActivationAsUpdate?: boolean;
    locationTracking?: boolean;
    logs?: boolean;
    sessionTimeout?: number;
    appVersion?: string;
    location?: MetricaLocation;
    preloadInfo?: MetricaPreloadInfo;
    installedAppCollecting?: boolean;
    // crashReporting?: boolean;
    // nativeCrashReporting?: boolean;
  }


  interface RNMetrica {
    activate: (config: MetricaConfig) => void;
    isActivated: () => Promise<boolean>;
    handleOpenURL: (url: string) => void;
    reportReferralUrl: (url: string) => void;
    reportEvent: (name: string, params?: Record<string, any>, onError?: (e: any) => void) => void;
    reportError: (name: string, reason?: string, onError?: (e: any) => void) => void;
    setLocation: (location?: MetricaLocation) => void;
    setLocationTracking: (enabled: boolean) => void;
    setProfileID: (id: string | null) => void;
  }

  const metrica: RNMetrica;

  export default metrica;
}
