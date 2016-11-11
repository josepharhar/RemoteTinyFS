// this file controls the processing, storage and accessing of the disk
var polyFS_visualizer = {};

polyFS_visualizer.data = {
   blocks: new Array(),

   Block: function(hdata, cdata) {
      this.blocktype = "super";
      this.hex_data = hdata;
      this.char_data = cdata;
      this.get_byte = function(index) {
         return parseInt(this.hex_data.substring(index * 2, index * 2 + 2), 16);
      }
   },

   bin2hex: function(s) {
      var i, l, o = '', n;
      console.log("converting binary to hex");
      for (i = 0, l = s.length; i < l; i++) {
         n = s[i].toString(16);
         o += n.length < 2 ? '0' + n : n;
      }

      return o;
   },

   bin2char: function(s) {
      var i, l, o = '', n;
      console.log("converting binary to char");
      for (i = 0, l = s.length; i < l; i++) {
         if(s[i] >= 32 && s[i] <= 127){
            n = String.fromCharCode(s[i]);
         }
         else {
            n = '.';
         }
         o += n;
      }

      return o;
   },

   process_data: function(diskdata) {
      console.log("processing");
      
      polyFS_visualizer.data.clear_disk();
      
      if(!diskdata.length){
         warning("Disk is empty!");
         return;
      }

      hexdata = polyFS_visualizer.data.bin2hex(diskdata); 
      chardata = polyFS_visualizer.data.bin2char(diskdata);

      // format the data as both hex and char
      var n = polyFS_visualizer.config.disk_config.block_size * 2;
      var hexblockdata = hexdata.match(new RegExp('.{1,'+ n +'}', 'g'));
      var n = polyFS_visualizer.config.disk_config.block_size;
      var charblockdata = chardata.match(new RegExp('.{1,'+ n +'}', 'g'));

      // create the blocks
      for (i = 0; i < hexblockdata.length; i++){
         polyFS_visualizer.data.blocks.push(new polyFS_visualizer.data.Block(hexblockdata[i], charblockdata[i], null));
      }

      return polyFS_visualizer.data.blocks;
   },

   clear_disk: function() {
      polyFS_visualizer.data.blocks = new Array();
   },

   get_block: function(index) {
      console.log("getting block " + index);
      if(index < 0 || index >= polyFS_visualizer.data.blocks.length){
         return null;
      }
      return polyFS_visualizer.data.blocks[index];
   },
}
