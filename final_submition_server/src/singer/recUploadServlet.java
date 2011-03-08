package singer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.apache.commons.io.IOUtils;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;


public class recUploadServlet extends HttpServlet{
	/*
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user==null){
			resp.sendRedirect("/listview.jsp");
			return;
		}


		PersistenceManager pm = PMF.get().getPersistenceManager();

		Statics stats=Statics.loadStatics(pm, user.getEmail());
		iReceipt rec = new iReceipt();
		rec.setUserID(user);
		rec.setStoreName(req.getParameter("business"));
		rec.setTotal(Double.parseDouble(req.getParameter("total")));
		rec.setCategory(req.getParameter("category"));
		rec.setDate_s(req.getParameter("date").replaceAll("/", "-"));
		rec.setNotes(req.getParameter("notes"));
		if((req.getParameter("flaged")!=null)&&(req.getParameter("flaged").equals("flaged")))
			rec.setFlaged(true);
		rec.setUpdate();
		rec.setUnique(stats.getUniqueId());
		stats.setUniqueId(stats.getUniqueId()-1);
		try{
		   ServletFileUpload upload = new ServletFileUpload();
		    FileItemIterator iter = upload.getItemIterator(req);
		    FileItemStream imageItm = iter.next();
		    InputStream imgStream = imageItm.openStream();

		    // construct our entity objects
		   rec.setFilepath(new Blob(IOUtils.toByteArray(imgStream)));
		}catch (FileUploadException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}



		stats.saveStatics(pm);
		rec.setSyncdate();

		pm.makePersistent(rec);
		resp.sendRedirect("/recview.jsp?id="+pm.getObjectId(rec));
		//resp.sendRedirect("/listview.jsp");
			pm.close();
		}
	 */

	private static byte[] transformImage(byte[] oldImageData){
		ImagesService imagesService = ImagesServiceFactory.getImagesService();

		Image oldImage = ImagesServiceFactory.makeImage(oldImageData);
		if (oldImage.getWidth()>400||oldImage.getHeight()>1000){
			float ratio=(float)oldImage.getWidth()/(float)oldImage.getHeight();
			Transform resize = ImagesServiceFactory.makeResize(400, Math.round(400/ratio));
			Image newImage = imagesService.applyTransform(resize, oldImage);
			return newImage.getImageData();
		}
		return oldImageData;


	}

	public static iReceipt getformdata(HttpServletRequest req) throws IOException, FileUploadException{
		iReceipt rec = new iReceipt();
		ServletFileUpload upload = new ServletFileUpload();
		//res.setContentType("text/plain");
		// res.setContentType("text/html");
		//  PrintWriter out = res.getWriter();
		// out.print("<html>\n<body>");
		FileItemIterator iterator = upload.getItemIterator(req);
		while (iterator.hasNext()) {
			FileItemStream item = iterator.next();
			InputStream stream = item.openStream();

			if (item.isFormField()) {
				String name=item.getFieldName();
				if (name.equals("id"))
					rec.setUnique(Integer.parseInt(Streams.asString(stream)));
				else if (name.equals("business"))
					rec.setStoreName(Streams.asString(stream));
				else if (name.equals("total"))
					rec.setTotal(Double.parseDouble(Streams.asString(stream)));
				else if (name.equals("category"))
					rec.setCategory(Streams.asString(stream));
				else if (name.equals("date"))
					rec.setDate_s(Streams.asString(stream).replaceAll("/", "-"));
				else if (name.equals("notes"))
					rec.setNotes(Streams.asString(stream));
				else if (name.equals("flaged"))
					if (Streams.asString(stream).equals("flaged"))
						rec.setFlaged(true);
				/*
          out.write("Got "+Streams.asString(stream)+" form field: " + item.getFieldName()+'\n');
				 */ } else 
					 if (checkapprovedMimeTypes(item.getName())){
						 Blob b=new Blob(transformImage(IOUtils.toByteArray(stream)));
						 rec.setFilepath(b);
					 }
		}
		return rec;
	}


	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user==null){
			res.sendRedirect("/listview.jsp");
			return;
		}
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Statics stats=Statics.loadStatics(pm, user.getEmail());


		/*	out.write("Got an uploaded file: " + item.getFieldName() +
                      ", name = " + item.getContentType()+'\n');

          // You now have the filename (item.getName() and the
          // contents (which you can read from stream). Here we just
          // print them back out to the servlet output stream, but you
          // will probably want to do something more interesting (for
          // example, wrap them in a Blob and commit them to the
          // datastore).
          int len;
         /* 
          Blob imageBlob = new Blob(IOUtils.toByteArray(stream));
  		MyImage myimage = new MyImage(item.getName(), imageBlob);
  		PersistenceManager pm = PMF.get().getPersistenceManager();
  		try {
  			pm.makePersistent(myimage);
  		} finally {
  			pm.close();
  		}
  	}/*
          byte[] buffer = new byte[8192];
          while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
            res.getOutputStream().write(buffer, 0, len);
          }
        }
      //}*/
		iReceipt rec;
		try {
			rec = getformdata(req);
		rec.setUserID(user);
		rec.setUpdate();
		rec.setSyncdate(new Date(0));
		rec.setUnique(stats.getUniqueId());
		stats.setUniqueId(stats.getUniqueId()-1);
		stats.setTotalexpenses(stats.getTotalexpenses()+rec.getTotal());
		stats.setMonthlyxpenses(rec.getRdate().getMonth(),rec.getRdate().getYear(),stats.getMonthlyxpenses()+rec.getTotal());
		stats.setYearlyxpenses(rec.getRdate().getYear(),stats.getYearlyxpenses()+rec.getTotal());
		stats.saveStatics(pm);
		pm.makePersistent(rec);
		res.sendRedirect("/recview.jsp?id="+pm.getObjectId(rec));
		//resp.sendRedirect("/listview.jsp");
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res.sendRedirect("/index.jsp");
		}
		finally{
		pm.close();
		}
	}


	private static boolean checkapprovedMimeTypes(String filename) {
		if (filename==null)
			return false;
		int index=filename.lastIndexOf('.');
		if (index==-1)
			return false;
		String mime=filename.substring(index);
		mime=mime.toUpperCase(Locale.ENGLISH); 
		String mimeTypes[]={".JPEG",".JPG", ".PNG", ".GIF", ".BMP", ".TIFF"};
		for (int i = 0; i < mimeTypes.length; i++) {
			if (mime.equals(mimeTypes[i]))
				return true;
		}
		return false;
	}
}






