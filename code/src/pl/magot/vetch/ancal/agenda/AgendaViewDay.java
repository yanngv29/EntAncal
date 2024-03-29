package pl.magot.vetch.ancal.agenda;


import java.util.ArrayList;
import pl.magot.vetch.ancal.AnCal;
import pl.magot.vetch.ancal.dataview.DataView;
import pl.magot.vetch.ancal.dataview.DataViewItem;
import pl.magot.vetch.ancal.views.*;
import android.graphics.Paint;
import android.widget.LinearLayout;


public class AgendaViewDay extends AgendaView
{	
	//fields
	private ArrayList<LinearLayout> vecHourItems = new ArrayList<LinearLayout>(); 

	//appointment click listener
	public ViewDayHourItem.OnItemClick onNewApptItemClick = new ViewDayHourItem.OnItemClick()
	{
		public void OnClick(ViewDayHourItem item)
		{
			doHourOfDayClick(item, ViewTodayItemHeader.ViewType.Appointments, item.GetHour(), item.GetMinutes());
		}
	};

	//fields
	private int iTextHeight = 0;
	private int iHourWidth = 0;
	private int iTimeWidth = 0;
	private int iUSTimeMarkWidth = 0;
	private int iSpaceWidthTime = 0;
	private int iSpaceWidthMinutes = 0; 
	private int iSpaceWidthUSTimeMark = 0;
	
//fields
	private Paint mpt = new Paint();
	
	//methods
	public AgendaViewDay(AnCal main)
	{
		super(main);
		iTextHeight = ViewDayHourItem.GetTextHeight(mpt);
		iHourWidth = ViewDayHourItem.GetSpaceWidthHour(mpt);
		iTimeWidth = ViewDayHourItem.GetSpaceWidthTime(mpt);
		iUSTimeMarkWidth = ViewDayHourItem.GetSpaceWidthUSTimeMark(mpt);
		iSpaceWidthTime = ViewTodayItemAppointment.GetSpaceWidthTime(mpt);
		iSpaceWidthMinutes = ViewTodayItemAppointment.GetSpaceWidthMinutes(mpt);
		iSpaceWidthUSTimeMark = ViewTodayItemAppointment.GetSpaceWidthUSTimeMark(mpt);
	}

	@Override
	public int GetViewType()
	{
		return viewMode.DAY;
	}
	
	@Override
	public int GetViewIndex()
	{
		return 1;
	}

	@Override
	public void Rebuild()
	{
		llayParent.removeAllViews();
		
		vecHourItems.clear();
				
		//build 24 hour day view
		for (int iHour = 0; iHour < 24; iHour++)
		{
			ViewDayHourItem item = new ViewDayHourItem(main, iHour, iTextHeight);
			item.SetItemData(main.prefs.b24HourMode, iHourWidth, iTimeWidth, iUSTimeMarkWidth);
			llayParent.addView(item);
			vecHourItems.add(iHour, item);
		}	
	}

	public void RemoveChildViewsFromAllHourItems()
	{
		for (int iHour = 0; iHour < vecHourItems.size(); iHour++)
		{
			LinearLayout item = vecHourItems.get(iHour);
			if (item != null)
				item.removeAllViewsInLayout();
		}	
		llayParent.invalidate();
	}

	@Override
	public void RebuildViewAppointments(DataView dataView)
	{
		final boolean bIsViewToday = IsViewToday();
		final int iCurrentHour = getTodayCurrentHour();

		RemoveChildViewsFromAllHourItems();

		//update time format
		for (int iHour = 0; iHour < vecHourItems.size(); iHour++)
		{
			ViewDayHourItem item = (ViewDayHourItem)vecHourItems.get(iHour);
			item.SetItemData(main.prefs.b24HourMode, iHourWidth, iTimeWidth, iUSTimeMarkWidth);			
			item.SetCurrentHour((bIsViewToday)?iCurrentHour:-1);			
			item.requestLayout();
		}
			
		//rebuild view
		for (int i = 0; i < dataView.GetRowsCountTotal(); i++)
		{
			DataViewItem row = dataView.GetRow(i, this.GetViewType());
			if (row != null)
			{				
				int iHour = row.GetStartHour();
				ViewTodayItemAppointment item = new ViewTodayItemAppointment(main);
				item.SetRowId(row.lID);
				item.SetItemTime(row.GetStartHour(), row.GetStartMinute(), true, main.prefs.b24HourMode, iSpaceWidthTime, iSpaceWidthMinutes, iSpaceWidthUSTimeMark);
				item.SetItemData(row.sSubject, row.bAlarm, row.IsRepeat());				
				item.SetItemClick(onApptItemClick);
							
				ViewDayHourItem hourItem = (ViewDayHourItem)vecHourItems.get(iHour);				
				if (hourItem != null)
				{
					hourItem.addView(item, lparams);
				}
			}
		}
			
		//update heights
		for (int iHour = 0; iHour < 24; iHour++)
		{
			ViewDayHourItem item = (ViewDayHourItem)vecHourItems.get(iHour);
			item.UpdateHeight();
			item.SetItemClick(onNewApptItemClick);
		}
	}
	
	@Override
	public void RebuildViewNotes(DataView dataView)
	{
	}

	@Override
	public void RebuildViewTasks(DataView dataView)
	{
	}

	@Override
	public void UpdateTimeFormat()
	{
		for (int iHour = 0; iHour < vecHourItems.size(); iHour++)
		{	
			ViewDayHourItem item = (ViewDayHourItem)vecHourItems.get(iHour);
			if (item != null)
				item.UpdateTimeFormat(main.prefs.b24HourMode, iHourWidth, iTimeWidth, iUSTimeMarkWidth);
		}	
	}
	
}
