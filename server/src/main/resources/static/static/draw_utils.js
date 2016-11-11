
polyFS_visualizer.draw_utils = {

   // helper function for drawing text
   draw_text: function(canvas, x, y, text, color, name){
      $('#' + canvas).drawText({
         layer: true,
         name: name,
         fillStyle: color,
         fontSize: '14pt',
         fontStyle: 'normal',
         fontFamily: 'Courier New',
         text: text,
         x: x,
         y: y,
         align: "left",
      });
   },

   // helper function for drawing a legend
   draw_legend: function(canvas, x, y, item_list, set){
      
      // LEGEND
      // horizontal padding
      var h_padding = 20;
      // padding around the block
      var padding = 10;
      // width of the block
      var width = 20;
      // corner
      var radius = 2;

      var block = 0;
      for(var item in item_list){

         // calculate the color
         var color = polyFS_visualizer.config.get_display_config(set, "fillstyle", item);

         // draw the rectangle
         $('#' + canvas).drawRect({
            layer: true,
            name: "symbol" + item,
            fillStyle: color,
            x: h_padding + x + 0.5, 
            y: block * (width + padding) + padding + y + 0.5,
            width: width,
            height: width,
            cornerRadius: radius,
            strokeStyle: '#000000',
            strokeWidth: 3,
         });
         
         // draw the label
         polyFS_visualizer.draw_utils.draw_text(canvas, width + h_padding * 2 + x, block * (width + padding) + padding + y, item, "#000000", "text" + item);

         var block = block + 1;
      }
      
   }
}
