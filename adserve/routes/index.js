
/*
 * GET home page.
 */

exports.index = function(req, res){

res.set({
  'status' : 200;
  'Content-Type': 'text/plain',
  'Content-Length': '123',
  'URL': 'http://pathtojpeg.com/jpeg';
  'href' : 'http://pathtolink.com';
  'uniqueID' : 'uniqueID';
  'appID' : '';
});

};