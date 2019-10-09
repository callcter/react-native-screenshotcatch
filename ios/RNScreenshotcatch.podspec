require 'json'
version = JSON.parse(File.read('../package.json'))["version"]

Pod::Spec.new do |s|
  s.name         = "RNScreenshotcatch"
  s.version      = version
  s.summary      = "RNScreenshotcatch"
  s.description  = <<-DESC
                  RNScreenshotcatch
                   DESC
  s.homepage     = "https://github.com/callcter/react-native-screenshotcatch"
  s.license      = "MIT"
  s.author       = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNScreenshotcatch.git", :tag => "master" }
  s.source_files = "RNScreenshotcatch/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"

end

  