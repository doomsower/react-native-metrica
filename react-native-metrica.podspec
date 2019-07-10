require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = 'react-native-metrica'
  s.version      = package['version']
  s.summary      = 'Yandex AppMetrica SDK for react-native'
  s.homepage     = 'https://github.com/doomsower/react-native-metrica#readme'
  s.license      = 'MIT'
  s.license      = { :type => 'MIT', :file => 'LICENSE' }
  s.author       = { 'Konstantin Kuznetsov' => 'K.Kuznetcov@gmail.com' }
  s.platform     = :ios, '9.0'
  s.source       = { :git => 'https://github.com/doomsower/react-native-metrica.git', :tag => 'master' }
  s.source_files  = 'ios/**/*.{h,m}'

  s.dependency 'React'
  s.dependency 'YandexMobileMetrica/Static/Core', '~>3.0'

end
