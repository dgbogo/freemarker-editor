[#setting datetime_format="iso"]
<html>
<head>
  <title>Test</title>
  <style>[#include "test.css" parse=false]</style>
</head>
<body>
  <div class="title">Welcome ${username}</div>
  <div>Template creation time: ${originalDatetime?datetime?string('dd/MM/yyyy HH:mm')}</div>
  <div>List example:
    <div>
    [#list 1..30 as counter]
      ${counter}
    [/#list]
    </div>
  </div>
  <div>Custom font test
    <div class="barcode">5465465</div>
  </div>
</body>
</html>